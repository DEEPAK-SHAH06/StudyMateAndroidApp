package com.example.studymateandroidapp.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository handling user authentication and Google Sign-In via Credentials Manager.
 */
class AuthRepository(val context: Context) {
    private val auth: FirebaseAuth = Firebase.auth
    private val credentialManager = CredentialManager.create(context)
    private val TAG = "AuthRepository"

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _currentUser

    /**
     * Deletes the current user account and all associated data from Firebase.
     */
    suspend fun deleteAccount(): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("No user logged in"))
        val userId = user.uid

        return try {
            Log.d(TAG, "Starting account deletion for user: $userId")

            // 1. Delete Firestore Data
            val firestore = Firebase.firestore
            val collections = listOf(
                "tasks", "goals", "sessions", "reflections", "achievements",
                "exams", "notes", "flashcards", "flashcard_reviews", "study_progress", "user_progress", "reminder_settings"
            )
            
            for (collectionName in collections) {
                val snapshot = firestore.collection("users").document(userId)
                    .collection(collectionName).get().await()
                for (doc in snapshot.documents) {
                    doc.reference.delete().await()
                }
                Log.d(TAG, "Deleted Firestore collection: $collectionName")
            }
            
            // Delete the user document itself if it exists
            firestore.collection("users").document(userId).delete().await()

            // 2. Delete Storage Data
            val storageRef = Firebase.storage.reference.child("profile_pictures/$userId.jpg")
            try {
                storageRef.delete().await()
                Log.d(TAG, "Deleted profile picture from Storage")
            } catch (e: Exception) {
                // Ignore if file doesn't exist or other non-critical storage errors
                Log.w(TAG, "Profile picture delete skipped: ${e.message}")
            }

            // 3. Delete Auth Account
            user.delete().await()
            _currentUser.value = null
            Log.d(TAG, "Firebase Auth account deleted successfully")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Delete account failed", e)
            Result.failure(e)
        }
    }

    /**
     * Triggers the Google Sign-In bottom sheet using modern Credentials Manager.
     */
    suspend fun signInWithGoogle(activity: android.app.Activity): Result<Unit> {
        if (!isNetworkAvailable()) {
            return Result.failure(Exception("Network connection error"))
        }

        return try {
            Log.d(TAG, "Starting Google Sign-In flow")

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("699079368997-1741hh0bnvrrg7df4nrm13fgbvgk7ufb.apps.googleusercontent.com")
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential

            Log.d(TAG, "Credential received: ${credential.type}")

            if (credential is GoogleIdTokenCredential) {
                val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
                auth.signInWithCredential(firebaseCredential).await()
                _currentUser.value = auth.currentUser
                Log.d(TAG, "Firebase Auth successful: ${auth.currentUser?.email}")
                Result.success(Unit)
            } else {
                Log.e(TAG, "Unsupported credential type: ${credential.type}")
                Result.failure(Exception("Unsupported credential type"))
            }
        } catch (e: NoCredentialException) {
            Log.e(TAG, "No credentials found. Ensure SHA-1 is added to Firebase.", e)
            Result.failure(Exception("No Google accounts found on this device or app is unauthorized."))
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "User cancelled sign-in")
            Result.failure(Exception("Sign-in cancelled"))
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in failed with unexpected error", e)
            Result.failure(e)
        }
    }

    /**
     * Authenticates a user using email and password.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        if (!isNetworkAvailable()) return Result.failure(Exception("No internet connection"))
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            _currentUser.value = auth.currentUser
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registers a new user with email and password.
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        if (!isNetworkAvailable()) return Result.failure(Exception("No internet connection"))
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            _currentUser.value = auth.currentUser
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sends a password reset email.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        if (!isNetworkAvailable()) return Result.failure(Exception("No internet connection"))
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    fun getUserId(): String? = auth.currentUser?.uid

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
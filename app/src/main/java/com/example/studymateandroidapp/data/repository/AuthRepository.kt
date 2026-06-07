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
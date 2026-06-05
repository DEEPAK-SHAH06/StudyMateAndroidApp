package com.example.studymateandroidapp.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import android.util.Log

object ProfileRepository {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val storage: FirebaseStorage get() = FirebaseStorage.getInstance()

    // Get current logged in user
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // Upload image to Firebase Storage + update Auth profile
    suspend fun updateProfile(username: String, imageUri: Uri?): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("User not logged in"))

            Log.d("PROFILE", "Starting profile update")

            val finalPhotoUrl = if (imageUri != null) {
                Log.d("PROFILE", "Uploading image")

                val ref = storage.reference
                    .child("profile_pictures/${user.uid}.jpg")

                ref.putFile(imageUri).await()

                Log.d("PROFILE", "Image uploaded")

                ref.downloadUrl.await()
            } else {
                user.photoUrl
            }

            Log.d("PROFILE", "Photo URL = $finalPhotoUrl")

            val builder = UserProfileChangeRequest.Builder()
                .setDisplayName(username)

            if (finalPhotoUrl != null) {
                builder.setPhotoUri(finalPhotoUrl)
            }

            val request = builder.build()

            user.updateProfile(request).await()

            Log.d("PROFILE", "Firebase Auth profile updated")

            user.reload().await()

            Log.d("PROFILE", "User reloaded")

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("PROFILE", "Update failed", e)
            Result.failure(e)
        }
    }
}

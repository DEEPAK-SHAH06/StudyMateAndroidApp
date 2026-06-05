package com.example.studymateandroidapp.data.model
import android.net.Uri
data class ProfileState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val currentUsername: String = "",
    val photoUrl: String? = null,
    val selectedImageUri: Uri? = null
)

package com.example.studymateandroidapp.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studymateandroidapp.R
import com.example.studymateandroidapp.ui.components.StudyMateTopBar
import com.example.studymateandroidapp.viewmodel.SettingViewmodel
import coil.compose.AsyncImage

@Composable
fun EditProfileScreen(
    viewModel: SettingViewmodel,
    onBack: () -> Unit
) {
    val profileState by viewModel.profileState.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setSelectedImageUri(it) }
    }

    LaunchedEffect(profileState.isSuccess) {
        if (profileState.isSuccess) {
            Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
            viewModel.resetProfileState()
            onBack()
        }
    }

    LaunchedEffect(profileState.errorMessage) {
        profileState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.resetProfileState()
        }
    }

    EditProfileContent(
        username = profileState.currentUsername,
        photoUrl = profileState.photoUrl,
        selectedImageUri = profileState.selectedImageUri,
        isLoading = profileState.isLoading,
        onBack = onBack,
        onPickImage = { imagePickerLauncher.launch("image/*") },
        onSaveProfile = { username ->
            viewModel.updateProfile(username, profileState.selectedImageUri)
        }
    )
}

@Composable
fun EditProfileContent(
    username: String,
    photoUrl: String?,
    selectedImageUri: Uri?,
    isLoading: Boolean,
    onBack: () -> Unit,
    onPickImage: () -> Unit,
    onSaveProfile: (String) -> Unit
) {
    var editedUsername by remember(username) { mutableStateOf(username) }

    Scaffold(
        topBar = {
            StudyMateTopBar(
                title = "Edit Profile",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Personalize your account with a photo. You can always change it later.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                // Main Circle
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = Color(0xFFF5F5F5),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (selectedImageUri != null || photoUrl != null) {
                            AsyncImage(
                                model = selectedImageUri ?: photoUrl,
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.add_image),
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }

                // Camera Icon / Pick Image Button
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(40.dp)
                        .clickable(enabled = !isLoading) { onPickImage() },
                    shape = CircleShape,
                    color = Color.Black,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.camera),
                            contentDescription = "Change Photo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Username",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color= MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = editedUsername,
                onValueChange = { editedUsername = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your new username") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            val isDirty = editedUsername != username || selectedImageUri != null

            Button(
                onClick = { onSaveProfile(editedUsername) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && editedUsername.isNotBlank() && isDirty
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Profile", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfilePreview() {
    MaterialTheme {
        EditProfileContent(
            username = "John Doe",
            photoUrl = null,
            selectedImageUri = null,
            isLoading = false,
            onBack = {},
            onPickImage = {},
            onSaveProfile = {}
        )
    }
}

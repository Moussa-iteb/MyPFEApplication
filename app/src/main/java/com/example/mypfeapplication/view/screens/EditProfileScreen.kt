package com.example.mypfeapplication.view.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypfeapplication.viewmodel.ProfileViewModel

val DarkGreen = Color(0xFF2D6A2D)

@Composable
fun EditProfileScreen(
    username: String = "User",
    email: String = "user@email.com",
    onBack: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf(username) }
    var emailValue by remember { mutableStateOf(email) }
    var phone by remember { mutableStateOf(viewModel.getPhone()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val isLoading by viewModel.isLoading.observeAsState(false)
    val updateSuccess by viewModel.updateSuccess.observeAsState(false)
    val error by viewModel.error.observeAsState()


    // إذا update نجح — ارجع للخلف
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            viewModel.resetState()
            onBack()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "EDIT PROFILE",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(100.dp)) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        androidx.compose.foundation.Image(
                            painter = coil.compose.rememberAsyncImagePainter(imageUri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = username.take(1).uppercase(),
                            fontSize = 40.sp,
                            color = DarkGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(DarkGreen)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change photo",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = username.uppercase(),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            Text(
                text = email.uppercase(),
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {

            ProfileTextField(
                label = "Full Name",
                value = fullName,
                onValueChange = { fullName = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = "Email Address",
                value = emailValue,
                onValueChange = { emailValue = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField(
                label = "Phone Number",
                value = phone,
                onValueChange = { phone = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // error message
            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // زر Save Changes
            Button(
                onClick = {
                    viewModel.updateProfile(fullName, emailValue, phone)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "SAVE CHANGES",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onChangePassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen)
            ) {
                Text(
                    text = "CHANGE PASSWORD",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                )
            ) {
                Text(
                    text = "LOGOUT",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, fontSize = 13.sp, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DarkGreen,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedLabelColor = DarkGreen
        )
    )
}
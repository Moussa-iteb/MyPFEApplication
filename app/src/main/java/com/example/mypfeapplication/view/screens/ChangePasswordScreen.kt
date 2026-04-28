package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypfeapplication.viewmodel.ChangePasswordViewModel

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit = {},
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val confirmMatch = confirmPassword.isNotEmpty() && confirmPassword == newPassword

    val isLoading by viewModel.isLoading.observeAsState(false)
    val success by viewModel.success.observeAsState(false)
    val error by viewModel.error.observeAsState()

    LaunchedEffect(success) {
        if (success) {
            viewModel.resetState()
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) {
                Text(text = "←", fontSize = 22.sp, color = DarkGreen)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CHANGE PASSWORD",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PasswordTextField(
            label = "CURRENT PASSWORD",
            value = currentPassword,
            onValueChange = { currentPassword = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        PasswordTextField(
            label = "NEW PASSWORD",
            value = newPassword,
            onValueChange = { newPassword = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = {
                Text(
                    text = "CONFIRM NEW PASSWORD",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                if (confirmMatch) {
                    Text(text = "✓", fontSize = 18.sp, color = DarkGreen)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkGreen,
                unfocusedBorderColor = if (confirmMatch) DarkGreen else Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = DarkGreen
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        error?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.changePassword(currentPassword, newPassword)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
            enabled = currentPassword.isNotEmpty()
                    && newPassword.isNotEmpty()
                    && confirmMatch
                    && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "CHANGE PASSWORD",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

// ← زيدناها هنا باش تحل مشكلة Unresolved reference
@Composable
fun PasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, fontSize = 12.sp, color = Color.Gray) },
        visualTransformation = PasswordVisualTransformation(),
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
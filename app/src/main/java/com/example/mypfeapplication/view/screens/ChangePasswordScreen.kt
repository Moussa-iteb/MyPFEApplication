package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit = {}
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val confirmMatch = confirmPassword.isNotEmpty() && confirmPassword == newPassword

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Header avec flèche retour
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

        // ✅ Current Password
        PasswordTextField(
            label = "CURRENT PASSWORD",
            value = currentPassword,
            onValueChange = { currentPassword = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ New Password
        PasswordTextField(
            label = "NEW PASSWORD",
            value = newPassword,
            onValueChange = { newPassword = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ✅ Confirm New Password — bordure verte si match
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
                focusedBorderColor = if (confirmMatch) DarkGreen else DarkGreen,
                unfocusedBorderColor = if (confirmMatch) DarkGreen else Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = DarkGreen
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ✅ Bouton Change Password
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
            enabled = currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmMatch
        ) {
            Text(
                text = "CHANGE PASSWORD",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

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
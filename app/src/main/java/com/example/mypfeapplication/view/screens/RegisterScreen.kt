package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.mypfeapplication.view.PurpleMain
import com.example.mypfeapplication.view.components.SocialButton
import com.example.mypfeapplication.viewmodel.RegisterViewModel

import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val authData by viewModel.authData.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    LaunchedEffect(authData) {
        if (authData != null) onRegisterSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(text = "Sign Up", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = "Create your SmartRide account", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        RegisterField("Username *", username, { username = it }, "ex: john_doe")
        Spacer(modifier = Modifier.height(16.dp))
        RegisterField("Email *", email, { email = it }, "ex: john@email.com")
        Spacer(modifier = Modifier.height(16.dp))
        RegisterField("Password *", password, { password = it }, "Minimum 8 characters", isPassword = true)
        Spacer(modifier = Modifier.height(16.dp))
        RegisterField("First Name (optional)", firstName, { firstName = it }, "ex: John")
        Spacer(modifier = Modifier.height(16.dp))
        RegisterField("Last Name (optional)", lastName, { lastName = it }, "ex: Doe")

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.register(
                    username = username, email = email, password = password,
                    firstName = firstName.ifEmpty { null },
                    lastName = lastName.ifEmpty { null }
                )
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleMain),
            enabled = !isLoading && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Text(text = "Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Sign up using", color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SocialButton(color = Color(0xFF1877F2), letter = "f")
            SocialButton(color = Color(0xFFDB4437), letter = "G")
            SocialButton(color = Color(0xFF0A66C2), letter = "in")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Already have an account? ", color = Color.Gray, fontSize = 14.sp)
            TextButton(onClick = onBackToLogin) {
                Text(text = "Sign In", color = PurpleMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun RegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Text(text = label, modifier = Modifier.fillMaxWidth(), color = Color.Gray, fontSize = 14.sp)
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = PurpleMain,
            unfocusedBorderColor = Color.LightGray
        )
    )
}
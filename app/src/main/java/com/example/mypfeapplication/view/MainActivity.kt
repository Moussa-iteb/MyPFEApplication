package com.example.mypfeapplication.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypfeapplication.repository.UserRepository
import com.example.mypfeapplication.viewmodel.LoginViewModel
import com.example.mypfeapplication.viewmodel.RegisterViewModel

val PurpleMain = Color(0xFF5C5EDD)

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Token géré dans Repository
        val repository = UserRepository(this)
        val startDestination = if (repository.getToken() != null) "home" else "welcome"

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {

                // Page Welcome
                composable("welcome") {
                    WelcomeScreen(
                        onLoginClick = { navController.navigate("login") },
                        onSignUpClick = { navController.navigate("register") }
                    )
                }

                // Page Login
                composable("login") {
                    LoginScreenNew(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            // ✅ Token déjà sauvegardé dans Repository
                            navController.navigate("home") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        },
                        onSignUpClick = { navController.navigate("register") }
                    )
                }

                // Page Register
                composable("register") {
                    RegisterScreenNew(
                        viewModel = registerViewModel,
                        onRegisterSuccess = {
                            // ✅ Token déjà sauvegardé dans Repository
                            navController.navigate("home") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        },
                        onBackToLogin = { navController.navigate("login") }
                    )
                }

                // Page Home
                composable("home") {
                    HomeScreen(
                        onLogout = {
                            repository.logout()
                            navController.navigate("welcome") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════
// WELCOME SCREEN
// ═══════════════════════════════
@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleMain)
    ) {
        // Logo en haut
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚡", fontSize = 60.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "SmartRide",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vélo Électrique Intelligent",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Carte blanche en bas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.BottomCenter)
                .background(Color.White, RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hello",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Welcome To SmartRide, where\nyou manage your daily rides",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleMain)
                ) {
                    Text(text = "Login", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onSignUpClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PurpleMain)
                ) {
                    Text(text = "Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PurpleMain)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Sign up using", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SocialButton(color = Color(0xFF1877F2), letter = "f")
                    SocialButton(color = Color(0xFFDB4437), letter = "G")
                    SocialButton(color = Color(0xFF0A66C2), letter = "in")
                }
            }
        }
    }
}

@Composable
fun SocialButton(color: Color, letter: String) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = letter, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

// ═══════════════════════════════
// LOGIN SCREEN
// ═══════════════════════════════
@Composable
fun LoginScreenNew(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    val authData by viewModel.authData.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ✅ Navigate après login — token déjà sauvegardé dans Repository
    LaunchedEffect(authData) {
        if (authData != null) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(text = "Login", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Email", modifier = Modifier.fillMaxWidth(), color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PurpleMain,
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Password", modifier = Modifier.fillMaxWidth(), color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PurpleMain,
                unfocusedBorderColor = Color.LightGray
            )
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = {}) {
                Text(text = "Forgot password?", color = PurpleMain, fontSize = 12.sp)
            }
        }

        error?.let {
            Text(text = it, color = Color.Red, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleMain),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Text(text = "Login", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Don't have an account? ", color = Color.Gray, fontSize = 14.sp)
            TextButton(onClick = onSignUpClick) {
                Text(text = "Sign Up", color = PurpleMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// ═══════════════════════════════
// REGISTER SCREEN
// ═══════════════════════════════
@Composable
fun RegisterScreenNew(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit = {},
    onBackToLogin: () -> Unit = {}
) {
    val authData by viewModel.authData.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    // ✅ Navigate après register — token déjà sauvegardé dans Repository
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

        Text(text = "Username *", modifier = Modifier.fillMaxWidth(), color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = username, onValueChange = { username = it },
            placeholder = { Text("ex: john_doe") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = PurpleMain, unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Email *", modifier = Modifier.fillMaxWidth(), color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            placeholder = { Text("ex: john@email.com") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = PurpleMain, unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Password *", modifier = Modifier.fillMaxWidth(), color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            placeholder = { Text("Minimum 8 characters") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = PurpleMain, unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "First Name (optional)", modifier = Modifier.fillMaxWidth(), color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = firstName, onValueChange = { firstName = it },
            placeholder = { Text("ex: John") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = PurpleMain, unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Last Name (optional)", modifier = Modifier.fillMaxWidth(), color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = lastName, onValueChange = { lastName = it },
            placeholder = { Text("ex: Doe") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = PurpleMain, unfocusedBorderColor = Color.LightGray
            )
        )

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

// ═══════════════════════════════
// HOME SCREEN
// ═══════════════════════════════
@Composable
fun HomeScreen(onLogout: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🚴 SmartRide", fontSize = 28.sp)
        Text(text = "Welcome !", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = PurpleMain)
        ) {
            Text("Logout")
        }
    }
}
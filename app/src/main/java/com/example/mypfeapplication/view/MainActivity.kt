package com.example.mypfeapplication.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypfeapplication.repository.UserRepository
import com.example.mypfeapplication.view.screens.*

val PurpleMain = Color(0xFF5C5EDD)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = UserRepository(this)
        val startDestination = if (repository.getToken() != null) "home" else "welcome"

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = startDestination) {

                composable("welcome") {
                    WelcomeScreen(
                        onLoginClick = { navController.navigate("login") },
                        onSignUpClick = { navController.navigate("register") }
                    )
                }

                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("home") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        },
                        onSignUpClick = { navController.navigate("register") }
                    )
                }

                composable("register") {
                    RegisterScreen(
                        onRegisterSuccess = {
                            navController.navigate("home") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        },
                        onBackToLogin = { navController.navigate("login") }
                    )
                }

                // ✅ Un seul composable("home") !
                composable("home") {
                    val localRepository = UserRepository(LocalContext.current)
                    HomeScreen(
                        username = localRepository.getUsername(),
                        hasBike = false, // ← false = sans vélo / true = avec vélo
                        onLogout = {
                            localRepository.logout()
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
package com.example.mypfeapplication.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypfeapplication.repository.UserRepository
import com.example.mypfeapplication.view.screens.HomeScreen
import com.example.mypfeapplication.view.screens.LoginScreen
import com.example.mypfeapplication.view.screens.WelcomeScreen
import com.example.mypfeapplication.view.screens.TripMapScreen

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
                        onSignUpClick = { navController.navigate("login") }
                    )
                }


                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("home") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }
                    )
                }

                composable("home") {
                    val localRepository = UserRepository(LocalContext.current)
                    HomeScreen(
                        username = localRepository.getUsername(),
                        hasBike = true,
                        onStartTrip = {
                            navController.navigate("trip_map")
                        },
                        onLogout = {
                            localRepository.logout()
                            navController.navigate("welcome") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }
                composable("trip_map") {
                    TripMapScreen(
                        onEndTrip = {
                            navController.navigate("home") {
                                popUpTo("trip_map") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
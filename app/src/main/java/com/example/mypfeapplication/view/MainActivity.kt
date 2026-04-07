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
import com.example.mypfeapplication.view.screens.BikeLocationScreen
import com.example.mypfeapplication.view.screens.ChangePasswordScreen
import com.example.mypfeapplication.view.screens.EditProfileScreen
import com.example.mypfeapplication.view.screens.HomeScreen
import com.example.mypfeapplication.view.screens.LoginScreen
import com.example.mypfeapplication.view.screens.NotificationsScreen
import com.example.mypfeapplication.view.screens.WelcomeScreen

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
                    HomeScreen(
                        onStartTrip = { navController.navigate("bike_location") },
                        onLogout = {
                            navController.navigate("welcome") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onEditProfile = { navController.navigate("edit_profile") },
                        onChangePassword = { navController.navigate("change_password") },
                        onNotifications = { navController.navigate("notifications") }
                    )
                }

                composable("bike_location") {
                    val localRepository = UserRepository(LocalContext.current)
                    BikeLocationScreen(
                        username = localRepository.getUsername(),
                        onEndTrip = {
                            navController.navigate("home") {
                                popUpTo("bike_location") { inclusive = true }
                            }
                        }
                    )
                }



                composable("change_password") {
                    ChangePasswordScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("notifications") {
                    NotificationsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
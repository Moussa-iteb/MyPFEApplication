package com.example.mypfeapplication.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mypfeapplication.repository.UserRepository
import com.example.mypfeapplication.view.screens.BikeLocationScreen
import com.example.mypfeapplication.view.screens.ChangePasswordScreen
import com.example.mypfeapplication.view.screens.EditProfileScreen
import com.example.mypfeapplication.view.screens.HomeScreen
import com.example.mypfeapplication.view.screens.QrScannerScreen
import com.example.mypfeapplication.view.screens.LoginScreen
import com.example.mypfeapplication.view.screens.NotificationsScreen
import com.example.mypfeapplication.view.screens.WelcomeScreen
import com.example.mypfeapplication.view.screens.ForgotPasswordScreen
import com.example.mypfeapplication.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val PurpleMain = Color(0xFF5C5EDD)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startDestination = if (repository.getToken() != null) {
            if (repository.getRole() == "admin") "admin_home" else "home"
        } else "welcome"

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
                        },
                        onForgotPassword = { navController.navigate("forgot_password") }
                    )
                }

                composable("home") {
                    val viewModel: HomeViewModel = hiltViewModel()
                    HomeScreen(
                        viewModel = viewModel,
                        onStartTrip = { navController.navigate("bike_location") },
                        onLogout = {
                            viewModel.logout()
                            navController.navigate("welcome") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onEditProfile = { navController.navigate("edit_profile") },
                        onChangePassword = { navController.navigate("change_password") },
                        onNotifications = { navController.navigate("notifications") },
                        onScanQr = { navController.navigate("qr_scanner") }
                    )
                }

                composable("admin_home") {
                    // admin screen
                }

                composable("bike_location") {
                    val viewModel: HomeViewModel = hiltViewModel()
                    BikeLocationScreen(
                        username = viewModel.getUsername(),
                        bikeId = viewModel.getBikeId(),
                        batteryLevel = viewModel.getBatteryLevel() / 100f,
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

                composable("forgot_password") {
                    ForgotPasswordScreen(
                        onBack = { navController.popBackStack() },
                        onPasswordResetSuccess = {
                            navController.navigate("login") {
                                popUpTo("forgot_password") { inclusive = true }
                            }
                        }
                    )
                }

                composable("notifications") {
                    NotificationsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("qr_scanner") { backStackEntry ->
                    val homeEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("home")
                    }
                    val viewModel: HomeViewModel = hiltViewModel(homeEntry)
                    QrScannerScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onScanSuccess = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}
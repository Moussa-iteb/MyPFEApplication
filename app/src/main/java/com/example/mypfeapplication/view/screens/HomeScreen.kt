package com.example.mypfeapplication.view.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypfeapplication.utils.LocationTracker
import com.example.mypfeapplication.view.components.home.*
import com.example.mypfeapplication.viewmodel.HomeViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onStartTrip: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    onEditProfile: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onScanQr: () -> Unit = {}
) {
    val username by viewModel.username.observeAsState("User")
    val hasBike by viewModel.hasBike.observeAsState(false)
    val selectedTab by viewModel.selectedTab.observeAsState(0)
    val showHistory by viewModel.showHistory.observeAsState(false)
    val assignedBike by viewModel.assignedBike.observeAsState()
    val email by viewModel.email.observeAsState("")

    // LocationTracker
    val context = LocalContext.current
    val locationTracker = remember { LocationTracker(context) }
    val currentAddress by locationTracker.currentAddress.collectAsState()
    val currentLocation by locationTracker.currentLocation.collectAsState()

    val permissions = remember {
        mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(Unit) {
        if (!multiplePermissionsState.allPermissionsGranted) {
            multiplePermissionsState.launchMultiplePermissionRequest()
        } else {
            locationTracker.startTracking()
        }
    }

    DisposableEffect(Unit) {
        onDispose { locationTracker.stopTracking() }
    }

    if (showHistory) {
        TripHistoryScreen(onBack = { viewModel.onBackFromHistory() })
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF0FBF6),
                        Color(0xFFE8F4FD),
                        Color(0xFFF0FBF6)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeColor = Color(0xFFD0E8F0)
            val stroke = Stroke(width = 1f)
            drawCircle(color = strokeColor, radius = size.width * 0.9f, style = stroke)
            drawCircle(color = strokeColor, radius = size.width * 0.7f, style = stroke)
            drawCircle(color = strokeColor, radius = size.width * 0.5f, style = stroke)
            drawCircle(color = strokeColor, radius = size.width * 0.3f, style = stroke)
            for (i in 0..8) {
                val y = size.height * i / 8f
                drawLine(
                    color = strokeColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 0.8f
                )
            }
            for (i in 0..5) {
                val x = size.width * i / 5f
                drawLine(
                    color = strokeColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 0.5f
                )
            }
        }

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { viewModel.onTabSelected(it) }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HomeHeader(
                    username = username,
                    notificationCount = 3,
                    onNotifications = onNotifications
                )

                when (selectedTab) {
                    0 -> if (hasBike) BikeAssociatedContent(
                        username = username,
                        bikeId = assignedBike?.id,
                        bikeBrand = assignedBike?.brand,
                        bikeModel = assignedBike?.model,
                        batteryLevel = assignedBike?.batteryLevel,
                        lastLocation = currentLocation?.let {
                            "%.4f° N, %.4f° E".format(it.first, it.second)
                        } ?: "Fetching location...",
                        lastAddress = currentAddress,
                        onViewHistory = { viewModel.onViewHistory() },
                        onStartTrip = onStartTrip,
                        onLogout = { viewModel.returnBike() }
                    ) else NoBikeContent(onScanQr = onScanQr)
                    1 -> ExploreScreen()
                    2 -> MyTripsScreen()
                    3 -> EditProfileScreen(
                        username = username,
                        email = email,
                        onChangePassword = onChangePassword,
                        onNotifications = onNotifications,
                        onLogout = {
                            viewModel.logout()
                            onLogout()
                        }
                    )
                }
            }
        }
    }
}
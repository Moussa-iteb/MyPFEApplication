package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypfeapplication.view.components.home.*
import com.example.mypfeapplication.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onStartTrip: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    // ✅ Observe les LiveData
    val username by viewModel.username.observeAsState("User")
    val hasBike by viewModel.hasBike.observeAsState(false)
    val selectedTab by viewModel.selectedTab.observeAsState(0)
    val showHistory by viewModel.showHistory.observeAsState(false)

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
                drawLine(color = strokeColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 0.8f)
            }
            for (i in 0..5) {
                val x = size.width * i / 5f
                drawLine(color = strokeColor, start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 0.5f)
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
                HomeHeader(username = username)
                when (selectedTab) {
                    0 -> if (hasBike) BikeAssociatedContent(
                        username = username,
                        onViewHistory = { viewModel.onViewHistory() },
                        onStartTrip = onStartTrip
                    ) else NoBikeContent()
                    1 -> ExploreScreen()
                    2 -> MyTripsScreen()
                    3 -> ProfileScreen(
                        username = username,
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
package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypfeapplication.utils.LocationTracker
import com.example.mypfeapplication.view.components.bike.*
import com.example.mypfeapplication.view.components.common.AppHeader
import com.example.mypfeapplication.view.components.map.BikeMap
import com.example.mypfeapplication.viewmodel.BikeViewModel
import com.google.android.gms.maps.model.LatLng

val RedMain = Color(0xFFE74C3C)
val SalmonMain = Color(0xFFE8756A)

@Composable
fun BikeLocationScreen(
    username: String = "User",
    onEndTrip: () -> Unit = {},
    bikeId: String = "",
    batteryLevel: Float = 0f,
    viewModel: BikeViewModel = hiltViewModel()
) {
    val bikeLocation = LatLng(36.8008, 10.1797)

    // ← context و locationTracker أولاً
    val context = LocalContext.current
    val locationTracker = remember { LocationTracker(context) }

    // ← بعدها باقي الـ val
    val tripStarted by viewModel.tripStarted.observeAsState(true)


    val locationAddress by locationTracker.currentAddress.collectAsState()

    LaunchedEffect(Unit) {
        locationTracker.startTracking()
    }

    DisposableEffect(Unit) {
        onDispose {
            locationTracker.stopTracking()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        BikeMap(bikeLocation = bikeLocation)

        Box(modifier = Modifier.align(Alignment.TopStart)) {
            AppHeader(
                username = username,
                tripStarted = tripStarted
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            BikeInfoCard(
                bikeId = bikeId,
                location = locationAddress,
                batteryLevel = batteryLevel,
                tripStarted = tripStarted,
                formattedTime = viewModel.getFormattedTime()
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!tripStarted) {
                SlideToStartButton(
                    onSlideComplete = { viewModel.startTrip() }
                )
            } else {
                EndTripButton(
                    onEndTrip = {
                        locationTracker.stopTracking()
                        viewModel.endTrip()
                        onEndTrip()
                    }
                )
            }
        }
    }
}
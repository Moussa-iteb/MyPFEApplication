package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: BikeViewModel = viewModel()
) {
    val bikeLocation = LatLng(36.8008, 10.1797)

    // ✅ Observe les LiveData
    val tripStarted by viewModel.tripStarted.observeAsState(true)
    val seconds by viewModel.seconds.observeAsState(0)
    val bikeId by viewModel.bikeId.observeAsState("ZE-0815")
    val batteryLevel by viewModel.batteryLevel.observeAsState(0.87f)
    val location by viewModel.location.observeAsState("Avenue de l'Indépendance")

    Box(modifier = Modifier.fillMaxSize()) {

        // ✅ Carte
        BikeMap(bikeLocation = bikeLocation)

        // ✅ Header
        Box(modifier = Modifier.align(Alignment.TopStart)) {
            AppHeader(
                username = username,
                tripStarted = tripStarted
            )
        }

        // ✅ Card + Boutons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            BikeInfoCard(
                bikeId = bikeId,
                location = location,
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
                        viewModel.endTrip()
                        onEndTrip()
                    }
                )
            }
        }
    }
}
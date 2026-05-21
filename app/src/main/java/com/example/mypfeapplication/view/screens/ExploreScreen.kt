package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypfeapplication.model.TripItem
import com.example.mypfeapplication.viewmodel.ExploreViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val trips     by viewModel.trips.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTrip by remember { mutableStateOf<TripItem?>(null) }

    // Default center — Tunisia
    val defaultCenter = LatLng(34.0, 9.0)
    val firstTripWithCoords = trips.firstOrNull { it.startLat != null && it.startLng != null }
    val mapCenter = firstTripWithCoords?.let {
        LatLng(it.startLat!!, it.startLng!!)
    } ?: defaultCenter

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapCenter, 7f)
    }

    // Update camera when trips load
    LaunchedEffect(firstTripWithCoords) {
        firstTripWithCoords?.let {
            cameraState.position = CameraPosition.fromLatLngZoom(
                LatLng(it.startLat!!, it.startLng!!), 8f
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ─── Header ───────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text("Explore", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(2.dp))
            Text("Available trips near you", fontSize = 14.sp, color = GrayText)
        }

        // ─── Google Map ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE8F4FD)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GreenMain)
                }
            } else {
                GoogleMap(
                    modifier            = Modifier.fillMaxSize(),
                    cameraPositionState = cameraState,
                    uiSettings          = MapUiSettings(
                        zoomControlsEnabled    = true,
                        myLocationButtonEnabled = false
                    )
                ) {
                    trips.forEach { trip ->
                        // Start marker — green
                        if (trip.startLat != null && trip.startLng != null) {
                            Marker(
                                state   = MarkerState(LatLng(trip.startLat, trip.startLng)),
                                title   = "Trip #${trip.id} — Start",
                                snippet = trip.startAddress ?: "—",
                                icon    = BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_GREEN
                                ),
                                onClick = {
                                    selectedTrip = trip
                                    false
                                }
                            )
                        }
                        // Destination marker — red
                        if (trip.destLat != null && trip.destLng != null) {
                            Marker(
                                state   = MarkerState(LatLng(trip.destLat, trip.destLng)),
                                title   = "Trip #${trip.id} — Destination",
                                snippet = trip.destinationAddress ?: "—",
                                icon    = BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_RED
                                ),
                                onClick = {
                                    selectedTrip = trip
                                    false
                                }
                            )
                        }
                        // Polyline start → destination
                        if (trip.startLat != null && trip.startLng != null &&
                            trip.destLat  != null && trip.destLng  != null) {
                            Polyline(
                                points = listOf(
                                    LatLng(trip.startLat, trip.startLng),
                                    LatLng(trip.destLat,  trip.destLng)
                                ),
                                color = GreenMain.copy(alpha = 0.6f),
                                width = 6f
                            )
                        }
                    }
                }
            }
        }

        // ─── Trip List ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Available Trips", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(
                    text  = "${trips.size} trips",
                    fontSize = 13.sp,
                    color = GrayText
                )
            }
            Spacer(Modifier.height(12.dp))

            if (isLoading) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenMain)
                }
            } else if (trips.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No available trips", color = GrayText, fontSize = 14.sp)
                }
            } else {
                trips.forEach { trip ->
                    TripExploreCard(trip = trip, onClick = { selectedTrip = trip })
                    Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    // ─── Bottom Sheet — Trip Detail ────────────────────────────────────
    selectedTrip?.let { trip ->
        ModalBottomSheet(
            onDismissRequest = { selectedTrip = null },
            containerColor   = Color.White,
            shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            TripDetailBottomSheet(trip = trip, onClose = { selectedTrip = null })
        }
    }
}

// ─── Trip Card ─────────────────────────────────────────────────────────────────

@Composable
fun TripExploreCard(trip: TripItem, onClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier              = Modifier.padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(GreenLight, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint               = GreenMain,
                        modifier           = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text       = "Trip #${trip.id}",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.Black
                    )
                    Text(
                        text     = "${trip.startAddress ?: "—"} → ${trip.destinationAddress ?: "—"}",
                        fontSize = 12.sp,
                        color    = GrayText,
                        maxLines = 1
                    )
                }
            }
            // Status badge
            Box(
                modifier = Modifier
                    .background(GreenLight, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text       = trip.status ?: "open",
                    fontSize   = 12.sp,
                    color      = GreenMain,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─── Bottom Sheet Content ──────────────────────────────────────────────────────

@Composable
fun TripDetailBottomSheet(trip: TripItem, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .padding(bottom = 32.dp)
    ) {
        // Header
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = "Trip #${trip.id}",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.Black
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = GrayText)
            }
        }

        Spacer(Modifier.height(4.dp))

        // Status badge
        Box(
            modifier = Modifier
                .background(GreenLight, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text       = trip.status?.uppercase() ?: "OPEN",
                fontSize   = 12.sp,
                color      = GreenMain,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))
        Spacer(Modifier.height(16.dp))

        // Route info
        InfoRow(label = "From",        value = trip.startAddress       ?: "—")
        Spacer(Modifier.height(10.dp))
        InfoRow(label = "To",          value = trip.destinationAddress ?: "—")
        Spacer(Modifier.height(10.dp))
        InfoRow(label = "Date",        value = formatDate(trip.startedAt))
        Spacer(Modifier.height(10.dp))
        InfoRow(label = "Distance",    value = if ((trip.distanceKm ?: 0.0) > 0) "%.1f km".format(trip.distanceKm) else "—")
        Spacer(Modifier.height(10.dp))
        InfoRow(label = "Participants",value = "${trip.tripUsers?.size ?: 0} users")

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, color = GrayText)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}
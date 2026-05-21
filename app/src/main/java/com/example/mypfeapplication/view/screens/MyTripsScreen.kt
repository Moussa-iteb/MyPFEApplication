package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypfeapplication.R
import com.example.mypfeapplication.model.UserTripData
import com.example.mypfeapplication.viewmodel.MyTripsViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MyTripsScreen(
    viewModel: MyTripsViewModel = hiltViewModel(),
    onTripClick: (UserTripData) -> Unit = {}
) {
    val trips by viewModel.trips.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val totalTrips   = trips.size

    // ✅ distance من الـ backend فقط — بدون تقدير
    val totalDistKm  = trips.sumOf { trip ->
        trip.distanceKm ?: 0.0
    }

    val totalMinutes = trips.sumOf { trip ->
        val tu = trip.tripUsers?.firstOrNull()
        calcMinutes(tu?.joinedAt, tu?.leftAt)
    }
    val totalHours = totalMinutes / 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("My Trips", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(Modifier.height(4.dp))
        Text("Your trip history", fontSize = 14.sp, color = GrayText)
        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Modifier.weight(1f), "$totalTrips",          "Total Trips",  R.drawable.ic_cycle, GreenMain)
            StatCard(Modifier.weight(1f), "%.1f km".format(totalDistKm), "Distance", R.drawable.regle,    Color(0xFFE74C3C))
            StatCard(Modifier.weight(1f), "${totalHours}h",       "Total Time",   R.drawable.duration, Color(0xFF3498DB))
        }

        Spacer(Modifier.height(24.dp))
        Text("Recent Trips", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenMain)
                }
            }
            trips.isEmpty() -> {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No trips yet", color = GrayText, fontSize = 14.sp)
                }
            }
            else -> {
                trips.forEach { trip ->
                    val tu       = trip.tripUsers?.firstOrNull()
                    val daysAgo  = daysAgo(trip.startedAt)
                    val mins     = calcMinutes(tu?.joinedAt, tu?.leftAt)

                    // ✅ distance من الـ backend فقط
                    val distKm   = trip.distanceKm ?: 0.0
                    val distText = if (distKm > 0) "%.1f km".format(distKm) else "—"

                    val timeText = if (mins >= 60) "${mins / 60}h ${mins % 60}m" else "${mins}m"
                    val points   = tu?.trackingPoints?.map { LatLng(it.latitude, it.longitude) } ?: emptyList()

                    Card(
                        modifier  = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onTripClick(trip) },
                        shape     = RoundedCornerShape(14.dp),
                        colors    = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column {
                            // ─── Google Map ────────────────────────────────────
                            if (points.size >= 2) {
                                val center = points[points.size / 2]
                                val camState = rememberCameraPositionState {
                                    position = CameraPosition.fromLatLngZoom(center, 14f)
                                }
                                GoogleMap(
                                    modifier            = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                                    cameraPositionState = camState,
                                    uiSettings          = MapUiSettings(
                                        zoomControlsEnabled     = false,
                                        scrollGesturesEnabled   = false,
                                        zoomGesturesEnabled     = false,
                                        rotationGesturesEnabled = false
                                    )
                                ) {
                                    Polyline(points = points, color = GreenMain, width = 8f)
                                    Marker(
                                        state = MarkerState(points.first()),
                                        icon  = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                                    )
                                    Marker(
                                        state = MarkerState(points.last()),
                                        icon  = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                                    )
                                }
                            }

                            // ─── Trip Info ─────────────────────────────────────
                            Row(
                                modifier              = Modifier.padding(16.dp),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(44.dp).background(GreenLight, RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter      = painterResource(R.drawable.ic_bicycling1),
                                            contentDescription = null,
                                            modifier     = Modifier.size(26.dp),
                                            contentScale = ContentScale.Fit,
                                            colorFilter  = ColorFilter.tint(GreenMain)
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text("Trip #${trip.id}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        Text("$daysAgo • $distText", fontSize = 12.sp, color = GrayText)
                                    }
                                }
                                Text(timeText, fontSize = 13.sp, color = GreenMain, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

fun daysAgo(dateStr: String?): String {
    if (dateStr == null) return "—"
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = sdf.parse(dateStr) ?: return "—"
        val days = (System.currentTimeMillis() - date.time) / (1000 * 60 * 60 * 24)
        when {
            days == 0L -> "Today"
            days == 1L -> "1 day ago"
            else       -> "$days days ago"
        }
    } catch (e: Exception) { "—" }
}

fun calcMinutes(start: String?, end: String?): Long {
    if (start == null || end == null) return 0L
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val s    = sdf.parse(start) ?: return 0L
        val e    = sdf.parse(end)   ?: return 0L
        val diff = e.time - s.time
        if (diff < 0) 0L else diff / (1000 * 60)
    } catch (e: Exception) { 0L }
}

@Composable
fun StatCard(modifier: Modifier, value: String, label: String, iconRes: Int, iconTint: Color = GreenMain) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = GreenLight),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter      = painterResource(id = iconRes),
                contentDescription = null,
                modifier     = Modifier.size(26.dp),
                contentScale = ContentScale.Fit,
                colorFilter  = ColorFilter.tint(iconTint)
            )
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GreenMain)
            Text(label, fontSize = 10.sp, color = GrayText, textAlign = TextAlign.Center)
        }
    }
}
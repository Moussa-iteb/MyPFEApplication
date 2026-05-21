package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypfeapplication.R
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripHistoryScreen(
    onBack: () -> Unit = {},
    viewModel: MyTripsViewModel = hiltViewModel()
) {
    val trips     by viewModel.trips.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val pathColors = listOf(
        Color(0xFF4CAF50), Color(0xFFBFA14A),
        Color(0xFF8B9E6A), Color(0xFF3498DB), Color(0xFFE74C3C)
    )

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF2C3E50), Color(0xFF34495E))))
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Text("SmartRide", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Text(
                        text       = "Trip History",
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                        modifier   = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C2833))
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                trips.isEmpty() -> {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No trips yet", color = Color.White, fontSize = 14.sp)
                    }
                }
                else -> {
                    trips.forEachIndexed { index, trip ->
                        val tu       = trip.tripUsers?.firstOrNull()
                        val mins     = calcMinutes(tu?.joinedAt, tu?.leftAt)
                        val distKm = trip.distanceKm ?: 0.0
                        val distText = if (distKm > 0) "%.1f km".format(distKm) else "—"

                        val timeText = if (mins >= 60) "${mins / 60}h ${mins % 60}m" else "${mins}m"
                        val dateText = formatDate(trip.startedAt)
                        val color    = pathColors[index % pathColors.size]
                        val points   = tu?.trackingPoints?.map { LatLng(it.latitude, it.longitude) } ?: emptyList()

                        TripCard(
                            tripId    = trip.id,
                            date      = dateText,
                            distance  = distText,
                            duration  = timeText,
                            pathColor = color,
                            points    = points
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

fun formatDate(dateStr: String?): String {
    if (dateStr == null) return "—"
    return try {
        val sdf  = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = sdf.parse(dateStr) ?: return "—"
        val out  = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.ENGLISH)
        out.format(date)
    } catch (e: Exception) { "—" }
}

@Composable
fun TripCard(
    tripId:    Int,
    date:      String,
    distance:  String,
    duration:  String,
    pathColor: Color,
    points:    List<LatLng> = emptyList()
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {

            // ─── Google Map أو Canvas ──────────────────────────────────
            if (points.size >= 2) {
                val center   = points[points.size / 2]
                val camState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(center, 14f)
                }
                GoogleMap(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    cameraPositionState = camState,
                    uiSettings          = MapUiSettings(
                        zoomControlsEnabled     = false,
                        scrollGesturesEnabled   = false,
                        zoomGesturesEnabled     = false,
                        rotationGesturesEnabled = false
                    )
                ) {
                    Polyline(points = points, color = pathColor, width = 8f)
                    Marker(
                        state = MarkerState(points.first()),
                        icon  = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
                    Marker(
                        state = MarkerState(points.last()),
                        icon  = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                }
            } else {
                // Canvas افتراضي إذا ما عندوش points
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        val path = Path().apply {
                            moveTo(10f, size.height * 0.8f)
                            cubicTo(
                                size.width * 0.3f, size.height * 0.2f,
                                size.width * 0.6f, size.height * 0.9f,
                                size.width - 10f,  size.height * 0.2f
                            )
                        }
                        drawPath(path, color = pathColor, style = Stroke(4f, cap = StrokeCap.Round))
                        drawCircle(Color(0xFF2ECC71), 8f, Offset(10f, size.height * 0.8f))
                        drawCircle(pathColor, 8f, Offset(size.width - 10f, size.height * 0.2f))
                    }
                }
            }

            // ─── Trip Info ─────────────────────────────────────────────
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Trip #$tripId", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Box(
                        modifier = Modifier.size(40.dp).background(Color(0xFFE8F8F0), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter      = painterResource(R.drawable.pp),
                            contentDescription = null,
                            modifier     = Modifier.size(24.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter  = ColorFilter.tint(GreenMain)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text("Trip #$tripId", fontSize = 13.sp, color = Color(0xFF555555))
                Text("Date: $date",   fontSize = 13.sp, color = Color(0xFF555555))

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    // Distance
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter      = painterResource(R.drawable.regle),
                                contentDescription = null,
                                modifier     = Modifier.size(20.dp),
                                contentScale = ContentScale.Fit,
                                colorFilter  = ColorFilter.tint(Color(0xFFE74C3C))
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Distance", fontSize = 11.sp, color = Color.Gray)
                            Text(distance, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color(0xFFEEEEEE)))

                    // Duration
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).background(Color(0xFFEBF5FB), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter      = painterResource(R.drawable.cc),
                                contentDescription = null,
                                modifier     = Modifier.size(20.dp),
                                contentScale = ContentScale.Fit,
                                colorFilter  = ColorFilter.tint(Color(0xFF3498DB))
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Duration", fontSize = 11.sp, color = Color.Gray)
                            Text(duration, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
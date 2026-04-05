package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mypfeapplication.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

val RedMain = Color(0xFFE74C3C)
val RedLight = Color(0xFFFFEBEE)

@Composable
fun TripMapScreen(
    onEndTrip: () -> Unit = {}
) {
    val bikeLocation = LatLng(36.8175, 10.1815)
    val userLocation = LatLng(36.8065, 10.1750)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bikeLocation, 14f)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Google Maps
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.NORMAL),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false
            )
        ) {
            Marker(
                state = MarkerState(position = bikeLocation),
                title = "Your Associated Bike",
                snippet = "Jardin du Belvédère"
            )
            Marker(
                state = MarkerState(position = userLocation),
                title = "Your Position"
            )
            Polyline(
                points = listOf(userLocation, bikeLocation),
                color = Color(0xFF1565C0),
                width = 8f
            )
        }

        // Header SmartRide
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "SmartRide",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A)
            )
        }

        // Card Stats + Bouton End Trip
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Live Trip Stats",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Distance 📏 → location drawable
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFF5F5F5), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.regle),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    contentScale = ContentScale.Fit,
                                    colorFilter = ColorFilter.tint(Color(0xFFE74C3C))
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "Distance", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "2.3 km",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        // Duration ⏱️ → ic_history drawable
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFF5F5F5), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.cc),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    contentScale = ContentScale.Fit,
                                    colorFilter = ColorFilter.tint(Color(0xFF3498DB))
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "Duration", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "14:32 min",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Speed 🚀 → ic_bicycling1 drawable
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFF5F5F5), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ss),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    contentScale = ContentScale.Fit,
                                    colorFilter = ColorFilter.tint(Color(0xFFE67E22))
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "Current Speed", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "18 km/h",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        // Battery 🔋 → ic_battery drawable
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_battery),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    contentScale = ContentScale.Fit,
                                    colorFilter = ColorFilter.tint(GreenMain)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(14.dp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(Color(0xFFEEEEEE))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(0.71f)
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(GreenMain)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "71% charged", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bouton End Trip — ⏻ → end_trip drawable
            Button(
                onClick = onEndTrip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedMain)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_eteindre),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "End Trip",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
package com.example.mypfeapplication.view.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.example.mypfeapplication.R
import com.example.mypfeapplication.view.screens.GrayText
import com.example.mypfeapplication.view.screens.GreenMain

private const val MAPS_API_KEY = "AIzaSyACxir1C1OBVF3hS_RYgiier6DnXfDtiaU"

@Composable
fun BikeAssociatedContent(
    username: String = "User",
    bikeId: Int? = null,
    bikeBrand: String? = null,
    bikeModel: String? = null,
    batteryLevel: Int? = null,
    lastLocation: String = "Fetching location...",
    lastAddress: String = "Fetching address...",
    onViewHistory: () -> Unit = {},
    onStartTrip: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    // بناء رابط الـ Static Map من الـ lastLocation
    val mapUrl = if (lastLocation != "Fetching location..." && lastLocation != "Unknown") {
        val coords = lastLocation
            .replace("° N", "")
            .replace("° E", "")
            .trim()
            .split(",")
        if (coords.size == 2) {
            val lat = coords[0].trim()
            val lng = coords[1].trim()
            "https://maps.googleapis.com/maps/api/staticmap?" +
                    "center=$lat,$lng" +
                    "&zoom=15&size=100x100" +
                    "&markers=color:red%7C$lat,$lng" +
                    "&key=$MAPS_API_KEY"
        } else null
    } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Hello, $username!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(text = "Your ride is ready.", fontSize = 15.sp, color = GrayText)

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "${bikeBrand ?: "Bike"} ${bikeModel ?: ""}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bike ID
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFFEBF5FB), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_cycle),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(Color(0xFF3498DB))
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Bike ID", fontSize = 12.sp, color = GrayText)
                            Text(
                                text = "B-${bikeId ?: "---"}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFF0F0F0)
                )

                // Battery
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFE8F8F0), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_battery),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(GreenMain)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Battery", fontSize = 12.sp, color = GrayText)
                        Text(
                            text = "${batteryLevel ?: "--"}% Charged",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFF0F0F0)
                )

                // Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFFE8F8F0), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(GreenMain, CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Status", fontSize = 12.sp, color = GrayText)
                            Text(
                                text = "Currently In Use",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenMain
                            )
                        }
                    }
                    Switch(
                        checked = true,
                        onCheckedChange = {},
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GreenMain
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFF0F0F0)
                )

                // Last Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.location),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(Color(0xFFE74C3C))
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Last Location", fontSize = 12.sp, color = GrayText)
                            Text(
                                text = lastLocation,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = lastAddress,
                                fontSize = 12.sp,
                                color = GrayText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Map image
                    if (mapUrl != null) {
                        AsyncImage(
                            model = mapUrl,
                            contentDescription = "Map",
                            modifier = Modifier
                                .size(65.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(65.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFD6EAF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🗺️", fontSize = 22.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onStartTrip,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenMain)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_bicycling1),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start Trip",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.stop),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color(0xFF555555))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "End Trip", color = Color(0xFF555555), fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            elevation = CardDefaults.cardElevation(2.dp),
            onClick = { onViewHistory() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color(0xFF555555))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "View Trip History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF555555)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
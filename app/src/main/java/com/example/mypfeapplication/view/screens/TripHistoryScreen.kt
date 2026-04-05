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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.mypfeapplication.R

data class TripItem(
    val title: String,
    val bikeId: String,
    val date: String,
    val distance: String,
    val duration: String,
    val pathColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripHistoryScreen(
    onBack: () -> Unit = {}
) {
    val trips = listOf(
        TripItem("Trip Summary A", "ZE-0815", "23 Oct 2023", "2.3 km", "14:32 min", Color(0xFF4CAF50)),
        TripItem("Trip Summary B", "BI-1192", "19 Oct 2023", "4.1 km", "20:18 min", Color(0xFFBFA14A)),
        TripItem("Trip Summary C", "CT-0043", "16 Oct 2023", "5.7 km", "45:10 min", Color(0xFF8B9E6A)),
        TripItem("Trip Summary D", "SM-2210", "10 Oct 2023", "3.2 km", "18:45 min", Color(0xFF4CAF50)),
        TripItem("Trip Summary E", "BK-0077", "05 Oct 2023", "6.8 km", "52:10 min", Color(0xFFBFA14A)),
    )

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2C3E50), Color(0xFF34495E))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                    val gridColor = Color(0x22FFFFFF)
                    for (i in 0..8) {
                        val x = size.width * i / 8f
                        drawLine(color = gridColor, start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 1f)
                    }
                    for (i in 0..4) {
                        val y = size.height * i / 4f
                        drawLine(color = gridColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "SmartRide",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Trip History",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
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
            trips.forEach { trip ->
                TripCard(trip = trip)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TripCard(trip: TripItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Title + Bike Icon  🚲 → ic_cycle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE8F8F0), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(GreenMain)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bike ID + Date + Mini Map
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Bike ID: ${trip.bikeId}", fontSize = 13.sp, color = Color(0xFF555555))
                    Text(text = "Date: ${trip.date}", fontSize = 13.sp, color = Color(0xFF555555))
                }

                // Mini Route Map (Canvas — pas d'emoji ici)
                Box(modifier = Modifier.size(width = 100.dp, height = 50.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val path = Path().apply {
                            moveTo(10f, size.height * 0.8f)
                            cubicTo(
                                size.width * 0.3f, size.height * 0.2f,
                                size.width * 0.6f, size.height * 0.9f,
                                size.width - 10f, size.height * 0.2f
                            )
                        }
                        drawPath(path = path, color = trip.pathColor, style = Stroke(width = 4f, cap = StrokeCap.Round))
                        drawCircle(color = Color(0xFF2ECC71), radius = 8f, center = Offset(10f, size.height * 0.8f))
                        drawCircle(color = trip.pathColor, radius = 8f, center = Offset(size.width - 10f, size.height * 0.2f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(12.dp))

            // Distance + Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Distance  📏 → location
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.regle),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(Color(0xFFE74C3C))
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Distance", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            text = trip.distance,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                // Divider vertical
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(Color(0xFFEEEEEE))
                )

                // Duration  ⏱️ → ic_history
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFEBF5FB), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cc),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(Color(0xFF3498DB))
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Duration", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            text = trip.duration,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
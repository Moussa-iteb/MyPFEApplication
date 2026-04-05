package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mypfeapplication.R

@Composable
fun MyTripsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(text = "My Trips", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Your trip history", fontSize = 14.sp, color = GrayText)

        Spacer(modifier = Modifier.height(24.dp))

        // Stats row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = "12",
                label = "Total Trips",
                iconRes = R.drawable.ic_cycle,         // 🚴 → ic_cycle
                iconTint = GreenMain
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = "47 km",
                label = "Distance",
                iconRes = R.drawable.regle,         // 📍 → location
                iconTint = Color(0xFFE74C3C)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = "8h",
                label = "Total Time",
                iconRes = R.drawable.duration,       // ⏱️ → ic_history
                iconTint = Color(0xFF3498DB)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Recent Trips", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))

        repeat(4) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(GreenLight, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            // 🚴 → ic_bicycling1
                            Image(
                                painter = painterResource(id = R.drawable.ic_bicycling1),
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(GreenMain)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Trip #${index + 1}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "${index + 1} day${if (index > 0) "s" else ""} ago • ${(index + 1) * 3}.${index}km",
                                fontSize = 12.sp,
                                color = GrayText
                            )
                        }
                    }
                    Text(
                        text = "${(index + 1) * 25} min",
                        fontSize = 13.sp,
                        color = GreenMain,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier,
    value: String,
    label: String,
    iconRes: Int,
    iconTint: Color = GreenMain
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GreenLight),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(iconTint)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GreenMain)
            Text(text = label, fontSize = 10.sp, color = GrayText, textAlign = TextAlign.Center)
        }
    }
}
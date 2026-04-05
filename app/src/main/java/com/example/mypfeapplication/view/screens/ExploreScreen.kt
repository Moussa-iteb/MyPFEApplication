package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExploreScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Text(text = "Explore", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Find available bikes near you", fontSize = 14.sp, color = GrayText)

        Spacer(modifier = Modifier.height(24.dp))

        // Map placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(0xFFE8F4FD), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🗺️", fontSize = 60.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Map Coming Soon", fontSize = 16.sp, color = GrayText)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Nearby Bikes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))

        repeat(3) { index ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
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
                            modifier = Modifier.size(44.dp).background(GreenLight, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🚲", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Bike B-${4810 + index}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(text = "${(index + 1) * 150}m away", fontSize = 12.sp, color = GrayText)
                        }
                    }
                    Box(
                        modifier = Modifier.background(GreenLight, RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = "${85 - index * 10}%", fontSize = 13.sp, color = GreenMain, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
package com.example.mypfeapplication.view.components.common

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mypfeapplication.view.screens.GreenMain

@Composable
fun TripTimer(running: Boolean = false) {
    var seconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(running) {
        if (running) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                seconds++
            }
        } else {
            seconds = 0
        }
    }

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    Text(
        text = "⏱ %02d:%02d:%02d".format(hours, minutes, secs),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = GreenMain
    )
}
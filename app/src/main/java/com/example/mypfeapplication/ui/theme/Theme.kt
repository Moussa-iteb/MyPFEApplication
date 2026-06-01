package com.example.mypfeapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
val DarkGreen = Color(0xFF1B5E20)
val GreenMain = Color(0xFF2ECC71)
@Composable
fun MyPFEApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}
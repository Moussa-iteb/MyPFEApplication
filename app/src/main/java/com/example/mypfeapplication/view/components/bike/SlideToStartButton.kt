package com.example.mypfeapplication.view.components.bike

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mypfeapplication.view.screens.GreenMain
import kotlin.math.roundToInt

@Composable
fun SlideToStartButton(onSlideComplete: () -> Unit = {}) {
    val buttonWidth = 320.dp
    val thumbSize = 52.dp
    val maxOffset = with(LocalDensity.current) {
        (buttonWidth - thumbSize - 8.dp).toPx()
    }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var isCompleted by remember { mutableStateOf(false) }

    val animatedOffset by animateFloatAsState(
        targetValue = if (isCompleted) maxOffset else offsetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "slideOffset"
    )

    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            kotlinx.coroutines.delay(400)
            onSlideComplete()
            offsetX = 0f
            isCompleted = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(GreenMain),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Slide to Start  ›››",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffset.roundToInt() + 4, 0) }
                .size(thumbSize)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.35f))
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX >= maxOffset * 0.80f) {
                                isCompleted = true
                            } else {
                                offsetX = 0f
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX = (offsetX + dragAmount).coerceIn(0f, maxOffset)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "›",
                fontSize = 26.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mypfeapplication.R

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1628))
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-60).dp, y = 80.dp)
                .blur(60.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00C853).copy(alpha = 0.7f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )


        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .blur(50.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF69F0AE).copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )


        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-20).dp, y = (-100).dp)
                .blur(40.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00E676).copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Logo ──
            Image(
                painter = painterResource(id = R.drawable.logo_smartride),
                contentDescription = "SmartRide Logo",
                modifier = Modifier.size(160.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Card Glassmorphism ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.10f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hello !",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Welcome To SmartRide, where\nyou manage your daily rides",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00C853)
                        )
                    ) {
                        Text(
                            text = "Login",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
package com.example.mypfeapplication.view
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.mypfeapplication.R
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val PurpleMain = Color(0xFF5C5EDD)

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WelcomeScreen(
                onLoginClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                },
                onSignUpClick = {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            )
        }
    }
}

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleMain)
    ) {
        // Logo at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_smartride),
                    contentDescription = "SmartRide Logo",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "E-Bike Management & Tracking",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // White card at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.BottomCenter)
                .background(
                    Color.White,
                    RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hello",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Welcome To SmartRide, where\nyou manage your daily rides",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleMain)
                ) {
                    Text(text = "Login", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sign Up Button
                OutlinedButton(
                    onClick = onSignUpClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PurpleMain)
                ) {
                    Text(text = "Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PurpleMain)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Sign up using", color = Color.Gray, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(12.dp))

                // Social Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SocialButton(color = Color(0xFF1877F2), letter = "f")
                    SocialButton(color = Color(0xFFDB4437), letter = "G")
                    SocialButton(color = Color(0xFF0A66C2), letter = "in")
                }
            }
        }
    }
}

@Composable
fun SocialButton(color: Color, letter: String) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = letter, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
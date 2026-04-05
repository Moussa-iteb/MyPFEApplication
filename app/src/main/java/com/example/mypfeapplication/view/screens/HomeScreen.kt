package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mypfeapplication.R

@Composable
fun HomeScreen(
    username: String = "User",
    hasBike: Boolean = false,
    onLogout: () -> Unit = {},
    onStartTrip: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showHistory by remember { mutableStateOf(false) }

    if (showHistory) {
        TripHistoryScreen(onBack = { showHistory = false })
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF0FBF6),
                        Color(0xFFE8F4FD),
                        Color(0xFFF0FBF6)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeColor = Color(0xFFD0E8F0)
            val stroke = Stroke(width = 1f)
            drawCircle(color = strokeColor, radius = size.width * 0.9f, style = stroke)
            drawCircle(color = strokeColor, radius = size.width * 0.7f, style = stroke)
            drawCircle(color = strokeColor, radius = size.width * 0.5f, style = stroke)
            drawCircle(color = strokeColor, radius = size.width * 0.3f, style = stroke)
            for (i in 0..8) {
                val y = size.height * i / 8f
                drawLine(color = strokeColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 0.8f)
            }
            for (i in 0..5) {
                val x = size.width * i / 5f
                drawLine(color = strokeColor, start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 0.5f)
            }
        }

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HomeHeader(username = username)
                when (selectedTab) {
                    // ✅ onStartTrip est maintenant passé correctement
                    0 -> if (hasBike) BikeAssociatedContent(
                        username = username,
                        onViewHistory = { showHistory = true },
                        onStartTrip = onStartTrip
                    ) else NoBikeContent()
                    1 -> ExploreScreen()
                    2 -> MyTripsScreen()
                    3 -> ProfileScreen(username = username, onLogout = onLogout)
                }
            }
        }
    }
}

@Composable
fun HomeHeader(username: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "SmartRide", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = GreenMain)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = username, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(GreenMain, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Online", fontSize = 11.sp, color = GreenMain)
                }
            }
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(GreenMain),
                contentAlignment = Alignment.Center
            ) {
                Text(text = username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// ═══════════════════════════════
// PAGE 1 — PAS DE VÉLO ASSOCIÉ
// ═══════════════════════════════
@Composable
fun NoBikeContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoscan),
                contentDescription = "Bike",
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                contentScale = ContentScale.Fit
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "You don't have a bike\nassociated yet.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Scan a bike to start your first trip",
                fontSize = 15.sp,
                color = GrayText,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenMain)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_qr),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Scan QR Code", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ═══════════════════════════════
// PAGE 2 — VÉLO ASSOCIÉ
// ═══════════════════════════════
@Composable
fun BikeAssociatedContent(
    username: String = "User",
    onViewHistory: () -> Unit = {},
    onStartTrip: () -> Unit = {}   // ✅ paramètre ajouté
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(text = "Hello, $username!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = "Your ride is ready.", fontSize = 15.sp, color = GrayText)

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Your Associated Bike", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                Spacer(modifier = Modifier.height(16.dp))

                // Bike ID
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(42.dp).background(Color(0xFFEBF5FB), RoundedCornerShape(10.dp)),
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
                            Text(text = "B-4815", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "2h 14m", fontSize = 13.sp, color = GrayText)
                        Text(text = "12.4 km", fontSize = 13.sp, color = GrayText)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))

                // Battery
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(42.dp).background(Color(0xFFE8F8F0), RoundedCornerShape(10.dp)),
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
                        Text(text = "87% Charged", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))

                // Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(42.dp).background(Color(0xFFE8F8F0), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier.size(16.dp).background(GreenMain, CircleShape))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Status", fontSize = 12.sp, color = GrayText)
                            Text(text = "Currently In Use", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GreenMain)
                        }
                    }
                    Switch(
                        checked = true,
                        onCheckedChange = {},
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenMain)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))

                // Last Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(42.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(10.dp)),
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
                            Text(text = "36.8065° N, 10.1815° E", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(text = "Tunis, Tunisia", fontSize = 12.sp, color = GrayText)
                        }
                    }
                    Box(
                        modifier = Modifier.size(65.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFD6EAF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🗺️", fontSize = 22.sp)
                            Text(text = "Jardin du\nBelvédère", fontSize = 7.sp, color = Color(0xFF1A5276), textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Start / End Trip
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onStartTrip,  // ✅ utilise le paramètre reçu
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
                Text(text = "Start Trip", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.end_trip),
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

        // View Trip History
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            elevation = CardDefaults.cardElevation(2.dp),
            onClick = { onViewHistory() }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
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
                Text(text = "View Trip History", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF555555))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ═══════════════════════════════
// BOTTOM NAVIGATION BAR
// ═══════════════════════════════
@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {

        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(imageVector = Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(24.dp))
            },
            label = { Text(text = "Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GreenMain, selectedTextColor = GreenMain,
                unselectedIconColor = GrayText, unselectedTextColor = GrayText,
                indicatorColor = GreenLight
            )
        )

        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_explore),
                    contentDescription = "Explore",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(if (selectedTab == 1) GreenMain else GrayText)
                )
            },
            label = { Text(text = "Explore", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = GreenMain, unselectedTextColor = GrayText, indicatorColor = GreenLight
            )
        )

        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.task),
                    contentDescription = "My Trips",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(if (selectedTab == 2) GreenMain else GrayText)
                )
            },
            label = { Text(text = "My Trips", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = GreenMain, unselectedTextColor = GrayText, indicatorColor = GreenLight
            )
        )

        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(if (selectedTab == 3) GreenMain else GrayText)
                )
            },
            label = { Text(text = "Profile", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = GreenMain, unselectedTextColor = GrayText, indicatorColor = GreenLight
            )
        )
    }
}
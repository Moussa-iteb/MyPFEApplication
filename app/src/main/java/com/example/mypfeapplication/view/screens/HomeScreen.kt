package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mypfeapplication.view.PurpleMain

// ═══════════════════════════════
// COULEURS
// ═══════════════════════════════
val GreenMain = Color(0xFF2ECC71)
val GrayLight = Color(0xFFF5F5F5)
val GrayText = Color(0xFF888888)

// ═══════════════════════════════
// HOME SCREEN PRINCIPALE
// ═══════════════════════════════
@Composable
fun HomeScreen(
    username: String = "User",
    hasBike: Boolean = false,
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
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
                .background(GrayLight)
                .padding(paddingValues)
        ) {
            HomeHeader(username = username)

            when (selectedTab) {
                0 -> if (hasBike) BikeAssociatedContent(username = username) else NoBikeContent()
                1 -> ExploreContent()
                2 -> MyTripsContent()
                3 -> ProfileContent(onLogout = onLogout)
            }
        }
    }
}

// ═══════════════════════════════
// HEADER
// ═══════════════════════════════
@Composable
fun HomeHeader(username: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "SmartRide",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PurpleMain
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = username,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(GreenMain, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Online", fontSize = 11.sp, color = GreenMain)
                }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PurpleMain),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color(0xFFEEF0FF), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🚲", fontSize = 80.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "You don't have a bike\nassociated yet.",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Scan a bike to start your first trip",
            fontSize = 15.sp,
            color = GrayText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurpleMain)
        ) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Scan QR Code",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// ═══════════════════════════════
// PAGE 2 — VÉLO ASSOCIÉ
// ═══════════════════════════════
@Composable
fun BikeAssociatedContent(username: String = "User") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Hello, $username!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Your ride is ready.",
            fontSize = 15.sp,
            color = GrayText
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = "Your Associated Bike",
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
                                .size(40.dp)
                                .background(Color(0xFFEEF0FF), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🚲", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Bike ID", fontSize = 12.sp, color = GrayText)
                            Text(
                                text = "B-4815",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "2h 14m", fontSize = 13.sp, color = GrayText)
                        Text(text = "12.4 km", fontSize = 13.sp, color = GrayText)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Color(0xFFF0F0F0))

                // Battery
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE8F8F0), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔋", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Battery", fontSize = 12.sp, color = GrayText)
                        Text(
                            text = "87% Charged",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Color(0xFFF0F0F0))

                // Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFE8F8F0), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
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

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Color(0xFFF0F0F0))

                // Last Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFFFEEEE), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📍", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Last Location", fontSize = 12.sp, color = GrayText)
                            Text(
                                text = "36.8065° N, 10.1815° E",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Tunis, Tunisia",
                                fontSize = 12.sp,
                                color = GrayText
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFE8F0FE), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🗺️", fontSize = 24.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Boutons Start / End Trip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenMain)
            ) {
                Text(text = "🚴 Start Trip", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            OutlinedButton(
                onClick = { },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text(text = "⏹ End Trip", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // View Trip History
        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF444444))
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = null,
                tint = Color(0xFF444444),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "View Trip History", fontSize = 15.sp, color = Color(0xFF444444))
        }
    }
}

// ═══════════════════════════════
// BOTTOM NAVIGATION BAR
// ═══════════════════════════════
@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val tabs = listOf(
            Triple("Home", Icons.Default.Home, 0),
            Triple("Explore", Icons.Default.Search, 1),
            Triple("My Trips", Icons.Default.List, 2),
            Triple("Profile", Icons.Default.Person, 3)
        )

        tabs.forEach { (label, icon, index) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PurpleMain,
                    selectedTextColor = PurpleMain,
                    unselectedIconColor = GrayText,
                    unselectedTextColor = GrayText,
                    indicatorColor = Color(0xFFEEF0FF)
                )
            )
        }
    }
}

// ═══════════════════════════════
// PAGES TEMPORAIRES
// ═══════════════════════════════
@Composable
fun ExploreContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "🗺️ Explore — Coming Soon", fontSize = 18.sp, color = GrayText)
    }
}

@Composable
fun MyTripsContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "🚴 My Trips — Coming Soon", fontSize = 18.sp, color = GrayText)
    }
}

@Composable
fun ProfileContent(onLogout: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "👤 Profile — Coming Soon", fontSize = 18.sp, color = GrayText)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = PurpleMain),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(text = "Logout", color = Color.White)
            }
        }
    }
}
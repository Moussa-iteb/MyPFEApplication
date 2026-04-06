package com.example.mypfeapplication.view.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mypfeapplication.R
import com.example.mypfeapplication.view.screens.GrayText
import com.example.mypfeapplication.view.screens.GreenLight
import com.example.mypfeapplication.view.screens.GreenMain

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(containerColor = androidx.compose.ui.graphics.Color.White, tonalElevation = 8.dp) {

        // Home
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(text = "Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GreenMain,
                selectedTextColor = GreenMain,
                unselectedIconColor = GrayText,
                unselectedTextColor = GrayText,
                indicatorColor = GreenLight
            )
        )

        // Explore
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_explore),
                    contentDescription = "Explore",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(
                        if (selectedTab == 1) GreenMain else GrayText
                    )
                )
            },
            label = { Text(text = "Explore", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = GreenMain,
                unselectedTextColor = GrayText,
                indicatorColor = GreenLight
            )
        )

        // My Trips
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.task),
                    contentDescription = "My Trips",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(
                        if (selectedTab == 2) GreenMain else GrayText
                    )
                )
            },
            label = { Text(text = "My Trips", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = GreenMain,
                unselectedTextColor = GrayText,
                indicatorColor = GreenLight
            )
        )

        // Profile
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(
                        if (selectedTab == 3) GreenMain else GrayText
                    )
                )
            },
            label = { Text(text = "Profile", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = GreenMain,
                unselectedTextColor = GrayText,
                indicatorColor = GreenLight
            )
        )
    }
}
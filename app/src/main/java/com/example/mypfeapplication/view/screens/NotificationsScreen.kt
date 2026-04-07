package com.example.mypfeapplication.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val isRead: Boolean = false
)

@Composable
fun NotificationsScreen(
    onBack: () -> Unit = {}
) {
    var notifications by remember {
        mutableStateOf(
            listOf(
                NotificationItem(1, "RIDE ALERT", "Bike available nearby."),
                NotificationItem(2, "RIDE ALERT", "Bike available nearby."),
                NotificationItem(3, "RIDE ALERT", "Bike available nearby."),
                NotificationItem(4, "RIDE ALERT", "Bike available nearby.")
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Header
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(text = "←", fontSize = 22.sp, color = DarkGreen)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "NOTIFICATIONS",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ✅ Liste notifications
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notifications) { notif ->
                NotificationCard(notification = notif)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ✅ Bouton Mark All As Read
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Button(
                onClick = {
                    notifications = notifications.map { it.copy(isRead = true) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Text(
                    text = "MARK ALL AS READ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                Color.White
            else
                Color(0xFFE8F5E9)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ Icône cloche verte
                Text(text = "🔔", fontSize = 24.sp)

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = notification.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkGreen
                    )
                    Text(
                        text = notification.message,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            // ✅ Séparateur vert
            HorizontalDivider(
                color = Color(0xFFB8D4B8),
                thickness = 1.dp
            )
        }
    }
}
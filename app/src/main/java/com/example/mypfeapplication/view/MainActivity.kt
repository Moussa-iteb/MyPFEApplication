package com.example.mypfeapplication.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mypfeapplication.ui.theme.MyPFEApplicationTheme
import com.example.mypfeapplication.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPFEApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UserScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun UserScreen(viewModel: UserViewModel) {
    val user by viewModel.user.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "MVVM Architecture", fontSize = 22.sp)
        Spacer(modifier = Modifier.height(20.dp))
        user?.let {
            Text(text = "Nom : ${it.name}", fontSize = 18.sp)
            Text(text = "Email : ${it.email}", fontSize = 16.sp)
        }
    }
}
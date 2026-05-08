package com.example.mypfeapplication.view.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mypfeapplication.viewmodel.HomeViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

enum class ScanMode { BIKE, TRIP }

@Composable
fun QrScannerScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    scanMode: ScanMode = ScanMode.BIKE,
    onBack: () -> Unit = {},
    onScanSuccess: () -> Unit = {}
) {
    val scanResult by viewModel.scanResult.observeAsState(initial = null)
    val isScanning by viewModel.isScanning.observeAsState(initial = false)
    val scanTripResult by viewModel.scanTripResult.observeAsState(initial = null)
    val isScanningTrip by viewModel.isScanningTrip.observeAsState(initial = false)

    var scanStarted by remember { mutableStateOf(false) }
    // ✅ Flag pour éviter double navigation
    var navigated by remember { mutableStateOf(false) }

    val currentScanMode = remember { scanMode }

    val loading = if (currentScanMode == ScanMode.BIKE) isScanning else isScanningTrip
    val resultSuccess = if (currentScanMode == ScanMode.BIKE) scanResult?.success else scanTripResult?.success
    val resultMessage = if (currentScanMode == ScanMode.BIKE) scanResult?.message else scanTripResult?.message

    val prompt = if (currentScanMode == ScanMode.BIKE)
        "Scan the bike QR Code"
    else
        "Scan the trip QR Code"

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { res ->
        android.util.Log.d("SCAN_MODE", "Mode: $currentScanMode, QR: ${res.contents}")
        if (res.contents != null) {
            when (currentScanMode) {
                ScanMode.BIKE -> viewModel.onBikeScanned(res.contents)
                ScanMode.TRIP -> viewModel.onTripScanned(res.contents)
            }
        } else {
            onBack()
        }
    }

    LaunchedEffect(Unit) {
        android.util.Log.d("SCAN_MODE", "Screen launched with mode: $currentScanMode")
        // ✅ Clear le résultat précédent au lancement pour éviter retrigger
        if (currentScanMode == ScanMode.BIKE) viewModel.clearScanResult()
        else viewModel.clearScanTripResult()

        if (!scanStarted) {
            scanStarted = true
            val options = ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                setPrompt(prompt)
                setBeepEnabled(true)
                setBarcodeImageEnabled(false)
                setCameraId(0)
                setOrientationLocked(false)
            }
            scanLauncher.launch(options)
        }
    }

    // ✅ Succès bike
    LaunchedEffect(scanResult) {
        if (currentScanMode == ScanMode.BIKE && scanResult?.success == true && !navigated) {
            navigated = true
            viewModel.setHasBike(true)
            viewModel.clearScanResult()
            onScanSuccess()
        }
    }

    // ✅ Succès trip
    LaunchedEffect(scanTripResult) {
        if (currentScanMode == ScanMode.TRIP && scanTripResult?.success == true && !navigated) {
            navigated = true
            viewModel.clearScanTripResult()
            onScanSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            loading -> {
                ScanLoadingScreen(
                    message = if (currentScanMode == ScanMode.BIKE)
                        "Checking bike availability..."
                    else
                        "Assigning trip..."
                )
            }
            resultSuccess == false -> {
                ScanErrorScreen(
                    message = resultMessage ?: "QR code not recognized",
                    onRetry = {
                        if (currentScanMode == ScanMode.BIKE) viewModel.clearScanResult()
                        else viewModel.clearScanTripResult()
                        onBack()
                    }
                )
            }
            else -> {
                ScanWaitingScreen(onBack = onBack)
            }
        }
    }
}

@Composable
private fun ScanLoadingScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1C2833)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF2ECC71), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message, color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
private fun ScanErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1C2833)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "⚠️", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Warning!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = message, fontSize = 15.sp, color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71))
                ) {
                    Text(text = "OK", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ScanWaitingScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1C2833)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF2ECC71), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Opening camera...", color = Color.White, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Back", color = Color.White)
            }
        }
    }
}
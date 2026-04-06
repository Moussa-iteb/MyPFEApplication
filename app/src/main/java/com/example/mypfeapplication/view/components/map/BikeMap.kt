package com.example.mypfeapplication.view.components.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun BikeMap(
    bikeLocation: LatLng,
    zoom: Float = 15f
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bikeLocation, zoom)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(mapType = MapType.NORMAL),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            compassEnabled = false
        )
    ) {
        Circle(
            center = bikeLocation,
            radius = 80.0,
            fillColor = Color(0x3300C853),
            strokeColor = Color(0x6600C853),
            strokeWidth = 2f
        )
        Marker(
            state = MarkerState(position = bikeLocation),
            title = "Your Bike",
            snippet = "Avenue de l'Indépendance",
            icon = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN
            )
        )
    }
}
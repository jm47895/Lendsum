package com.lendsumapp.lendsum.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ProfileScreen() {
    ProfileContent()
}

@Composable
fun ProfileContent(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Green)
    )
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun ProfileContentPreview() {
    ProfileContent()
}
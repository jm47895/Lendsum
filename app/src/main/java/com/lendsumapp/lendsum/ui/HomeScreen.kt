package com.lendsumapp.lendsum.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController
){
    HomeScreenContent()

}

@Composable
fun HomeScreenContent(){
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)){

    }
}

@Preview
@Composable
fun HomeScreenContentPreview(){
    HomeScreenContent()
}
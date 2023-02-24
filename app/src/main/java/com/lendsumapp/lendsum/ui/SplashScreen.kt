package com.lendsumapp.lendsum.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.ui.theme.ColorPrimary
import com.lendsumapp.lendsum.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController
){

    val loginViewModel = hiltViewModel<LoginViewModel>()

    LaunchedEffect(Unit){
        delay(1500)
        loginViewModel.firebaseUser?.let { navController.navigate(NavDestination.HOME.key) } ?: navController.navigate(NavDestination.LOGIN.key)
    }

    SplashScreenContent()

}

@Composable
fun SplashScreenContent(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPrimary),
        contentAlignment = Alignment.Center
    ){

        Icon(painter = painterResource(id = R.drawable.ic_handshake_24), contentDescription = "Splash Icon", tint = Color.White)
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun SplashScreenContentPreview(){
    SplashScreenContent()
}
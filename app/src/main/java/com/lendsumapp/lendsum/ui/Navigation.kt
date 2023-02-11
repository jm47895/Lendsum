package com.lendsumapp.lendsum.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun LendsumNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavDestination.LOGIN.destination
){
    NavHost(modifier = modifier, navController = navController, startDestination = startDestination){
        composable(NavDestination.LOGIN.destination){
            LoginScreen(
                onForgotPasswordClicked = { navController.navigate(NavDestination.PASSWORD_RESET.destination)}
            )
        }
        composable(NavDestination.PASSWORD_RESET.destination){
            ForgotPasswordScreen(
                onBackButtonClicked = {
                    navController.navigateUp()
                },
                onSendResetClicked = { navController.navigateUp() }
            )
        }
    }
}

enum class NavDestination(val destination: String){
    LOGIN("login"),
    CREATE_ACCOUNT("create_account"),
    PASSWORD_RESET("password_reset")
}
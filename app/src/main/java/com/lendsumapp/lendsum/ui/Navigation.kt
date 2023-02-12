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
    startDestination: String = NavDestination.LOGIN.key
){
    NavHost(modifier = modifier, navController = navController, startDestination = startDestination){
        composable(route = NavDestination.LOGIN.key){
            LoginScreen(
                navController = navController
            )
        }
        composable(route = NavDestination.PASSWORD_RESET.key){
            ForgotPasswordScreen(
                navController = navController
            )
        }
        composable(route = NavDestination.HOME.key){
            HomeScreen(
                navController = navController
            )
        }
    }
}

enum class NavDestination(val key: String){
    LOGIN("login"),
    CREATE_ACCOUNT("create_account"),
    PASSWORD_RESET("password_reset"),
    HOME("home")
}
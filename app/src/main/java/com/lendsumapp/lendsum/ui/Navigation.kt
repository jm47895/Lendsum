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
    startDestination: String = NavDestination.SPLASH_SCREEN.key
){
    NavHost(modifier = modifier, navController = navController, startDestination = startDestination){
        composable(route = NavDestination.SPLASH_SCREEN.key){
            SplashScreen(
                navController = navController
            )
        }
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
        composable(route = NavDestination.CREATE_ACCOUNT.key){
            CreateAccountScreen(
                navController = navController
            )
        }
        composable(route = NavDestination.NUMBER_VERIFICATION.key){
            NumberVerificationScreen(
                navController = navController
            )
        }
    }
}

enum class NavDestination(val key: String){
    SPLASH_SCREEN("splash_screen"),
    LOGIN("login"),
    CREATE_ACCOUNT("create_account"),
    PASSWORD_RESET("password_reset"),
    NUMBER_VERIFICATION("number_verification"),
    HOME("home")
}
package com.lendsumapp.lendsum.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.ui.theme.ColorPrimary

@Composable
fun HomeScreen(
    navController: NavController
){
    HomeScreenContent(
        onSettingsClicked = { navController.navigate(NavDestination.SETTINGS.key) }
    )
}

@Composable
fun HomeScreenContent(
    onSettingsClicked:() -> Unit
){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)){

        var currentScreen by remember { mutableStateOf(BottomNavOption.PROFILE) }

        when(currentScreen){
            BottomNavOption.MESSAGES -> MessagesScreen()
            BottomNavOption.BUNDLES -> BundlesScreen()
            BottomNavOption.PROFILE -> ProfileScreen(
                onSettingsClicked = onSettingsClicked
            )
            BottomNavOption.SERVICES -> ServicesScreen()
            BottomNavOption.NOTIFICATIONS -> NotificationsScreen()
        }

        BottomNavigation(
            selectedScreen = currentScreen,
            modifier = Modifier.align(Alignment.BottomCenter),
            onOptionClicked = {
                currentScreen = it
            }
        )

    }
}
@Composable
fun BottomNavigation(
    selectedScreen: BottomNavOption,
    modifier: Modifier = Modifier,
    onOptionClicked:(BottomNavOption) -> Unit
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(.05f)
            .background(ColorPrimary),
    ) {
        BottomNavOption.values().forEach {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .clickable {
                        onOptionClicked(it)
                    }
                    .border(1.dp, color = Color.White)
                    .background(color = if (it == selectedScreen) Color.White else ColorPrimary),
                contentAlignment = Alignment.Center
            ){
                Icon(painter = painterResource(id = it.drawableRes), contentDescription = "BottomNavIcon", tint = if(it == selectedScreen) ColorPrimary else Color.White)
            }
        }
    }
}

enum class BottomNavOption(val drawableRes: Int){
    MESSAGES(R.drawable.ic_message_24),
    BUNDLES(R.drawable.ic_bundle_24),
    PROFILE(R.drawable.ic_profile_24),
    SERVICES(R.drawable.ic_handshake_24),
    NOTIFICATIONS(R.drawable.ic_notifications_24)
}

@Preview
@Composable
fun HomeScreenContentPreview(){
    HomeScreenContent(
        onSettingsClicked = {}
    )
}
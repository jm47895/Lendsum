package com.lendsumapp.lendsum.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.ui.components.BackButton
import com.lendsumapp.lendsum.ui.theme.ColorPrimary
import com.lendsumapp.lendsum.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController
) {

    val settingsViewModel = hiltViewModel<SettingsViewModel>()

    SettingsScreenContent(
        onBackClicked = { navController.navigateUp() },
        onAccountClicked = {
            navController.navigate(NavDestination.ACCOUNT.key)
        },
        onLogoutClicked = {
            settingsViewModel.logOut()
            navController.navigate(NavDestination.LOGIN.key)
        }
    )
}

@Composable
fun SettingsScreenContent(
    onBackClicked:() -> Unit,
    onAccountClicked:() -> Unit,
    onLogoutClicked:() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Top
        ){
            BackButton(color = Color.White) {
                onBackClicked.invoke()
            }
            SettingsOptions(
                onAccountClicked = onAccountClicked,
                onLogoutClicked = onLogoutClicked
            )
        }
    }
}

@Composable
fun SettingsOptions(
    onAccountClicked:() -> Unit,
    onLogoutClicked:() -> Unit
){
    SettingsOption.values().forEach { setting ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
                .clickable {
                    when (setting) {
                        SettingsOption.ACCOUNT -> onAccountClicked.invoke()
                        SettingsOption.LOGOUT -> onLogoutClicked.invoke()
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = setting.iconDrawable),
                contentDescription = "Settings Icon",
                tint = ColorPrimary
            )
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(id = setting.title).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = setting.description),
                    color = Color.White
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(1f),
                contentAlignment = Alignment.CenterEnd
            ){
                Icon(painter = painterResource(id = R.drawable.ic_settings_right_continue_32), contentDescription = "Settings Continue", tint = ColorPrimary)
            }
        }
    }
}

enum class SettingsOption(@DrawableRes val iconDrawable: Int, @StringRes val title: Int, @StringRes val description: Int){
    ACCOUNT(R.drawable.ic_account_circle_32, R.string.account, R.string.account_settings_desc),
    LOGOUT(R.drawable.ic_logout_32, R.string.sign_out, R.string.sign_out)
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun SettingsScreenPreview() {
    SettingsScreenContent(
        onBackClicked = {},
        onAccountClicked = {},
        onLogoutClicked = {}
    )
}
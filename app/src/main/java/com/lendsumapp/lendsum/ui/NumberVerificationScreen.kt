package com.lendsumapp.lendsum.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.ui.components.*

@Composable
fun NumberVerificationScreen(
    navController: NavController
){
    NumberVerificationContent(
        onBackButtonPressed = { navController.navigateUp() },
        onSendCodeClicked = {},
        onVerifyCodeClicked = {}
    )
}

@Composable
fun NumberVerificationContent(
    onBackButtonPressed:() -> Unit,
    onSendCodeClicked:() -> Unit,
    onVerifyCodeClicked:() -> Unit
){
    val focusManager = LocalFocusManager.current
    var expandDropdown by remember { mutableStateOf(false) }
    val numPattern by remember { mutableStateOf(Regex("^\\d+\$")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    expandDropdown = false
                })
            }
            .padding(8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        BackButton(
            color = Color.Black
        ) {
            onBackButtonPressed.invoke()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ){

            CountryCodePicker(
                defaultLabel = "${CountryCode.US.name} ${CountryCode.US.countryCode}",
                expandDropdown = expandDropdown,
                countryCodeList = CountryCode.values().toList(),
                onCountryCodeChanged = {
                    expandDropdown = false
                },
                onDropDownToggled = { expandDropdown = !expandDropdown }
            )
            LendsumField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { if (it.hasFocus) expandDropdown = false },
                keyBoardType = KeyboardType.Number,
                supportingLabel = stringResource(id = R.string.enter_phone_number),
                regEx = numPattern,
                onTextChanged = {

                }
            )
        }
        LendsumButton(
            modifier = Modifier
                .fillMaxWidth(.60f)
                .align(Alignment.End),
            text = stringResource(id = R.string.send_verification_code).uppercase()
        ){
            onSendCodeClicked.invoke()
        }
        LendsumField(
            keyBoardType = KeyboardType.Number,
            supportingLabel = stringResource(id = R.string.enter_code),
            regEx = numPattern,
            onTextChanged = {}
        )
        LendsumButton(
            modifier = Modifier
                .fillMaxWidth(.30f)
                .align(Alignment.End),
            text = stringResource(R.string.verify).uppercase()
        ){
            onVerifyCodeClicked.invoke()
        }

    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun NumberVerificationContentPreview(){
    NumberVerificationContent(
        onBackButtonPressed = {},
        onSendCodeClicked =  {},
        onVerifyCodeClicked = {}
    )
}
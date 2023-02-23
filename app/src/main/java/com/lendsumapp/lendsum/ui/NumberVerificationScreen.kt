package com.lendsumapp.lendsum.ui

import android.app.Activity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.ui.components.*
import com.lendsumapp.lendsum.viewmodel.NumberVerificationViewModel

@Composable
fun NumberVerificationScreen(
    navController: NavController
){

    val numberVerificationViewModel = hiltViewModel<NumberVerificationViewModel>()
    val context = LocalContext.current as Activity

    if(numberVerificationViewModel.phoneLinkState.status == Status.SUCCESS){
        numberVerificationViewModel.resetLinkStatus()
        numberVerificationViewModel.insertNewUserIntoSqlCache()
        numberVerificationViewModel.insertNewUserIntoFirestoreDb()
        navController.navigate(NavDestination.HOME.key)
    }

    NumberVerificationContent(
        phoneCodeState = numberVerificationViewModel.phoneCodeState,
        phoneLinkState = numberVerificationViewModel.phoneLinkState,
        onBackButtonPressed = { navController.navigateUp() },
        onSendCodeClicked = { number ->
            numberVerificationViewModel.sendSMSCode(number, context)

        },
        onVerifyCodeClicked = { inputCode ->
            numberVerificationViewModel.verifyPhoneNumber(inputCode)
        },
        onCodeFieldChanged = {
            numberVerificationViewModel.resetLinkStatus()
        },
        onNumberFieldChanged = {
            numberVerificationViewModel.resetPhoneCodeStatus()
        }
    )
}

@Composable
fun NumberVerificationContent(
    phoneCodeState: Response<String>,
    phoneLinkState: Response<Unit>,
    onBackButtonPressed:() -> Unit,
    onSendCodeClicked:(String) -> Unit,
    onVerifyCodeClicked:(String) -> Unit,
    onCodeFieldChanged:() -> Unit,
    onNumberFieldChanged:() -> Unit
){
    val focusManager = LocalFocusManager.current
    var expandDropdown by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var inputCode by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf(CountryCode.US.countryCode) }
    val numPattern by remember { mutableStateOf(Regex("^\\d+\$")) }

    if(phoneCodeState.status == Status.LOADING || phoneLinkState.status == Status.LOADING){
        LoadingAnimation()
    }

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
                    countryCode = it.countryCode
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
                errorLabel = when(phoneCodeState.error)
                {
                    LendsumError.INVALID_PHONE_CREDENTIAL -> stringResource(id = R.string.phone_number_invalid)
                    LendsumError.SMS_LIMIT_MET -> stringResource(id = R.string.exceeded_code_attempts)
                    else -> null
                },
                regEx = numPattern,
                onTextChanged = {
                    phoneNumber = it
                    onNumberFieldChanged.invoke()
                }
            )
        }
        LendsumButton(
            modifier = Modifier
                .fillMaxWidth(.60f)
                .align(Alignment.End),
            text = stringResource(id = R.string.send_verification_code).uppercase()
        ){
            onSendCodeClicked(countryCode + phoneNumber)
        }
        LendsumField(
            keyBoardType = KeyboardType.Number,
            supportingLabel = stringResource(id = R.string.enter_code),
            errorLabel = when
            {
                phoneLinkState.error == LendsumError.INVALID_PHONE_CREDENTIAL -> stringResource(id = R.string.code_not_match)
                phoneCodeState.error == LendsumError.PHONE_CODE_TIMED_OUT -> stringResource(id = R.string.code_timeout)
                else ->{ null }
            },
            regEx = numPattern,
            onTextChanged = {
                onCodeFieldChanged.invoke()
                inputCode = it
            }
        )
        LendsumButton(
            modifier = Modifier
                .fillMaxWidth(.30f)
                .align(Alignment.End),
            text = stringResource(R.string.verify).uppercase()
        ){
            onVerifyCodeClicked(inputCode)
        }

    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun NumberVerificationContentPreview(){
    NumberVerificationContent(
        phoneCodeState = Response(),
        phoneLinkState = Response(),
        onBackButtonPressed = {},
        onSendCodeClicked =  {},
        onVerifyCodeClicked = {},
        onCodeFieldChanged = {},
        onNumberFieldChanged = {}
    )
}
package com.lendsumapp.lendsum.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.ui.components.BackButton
import com.lendsumapp.lendsum.ui.components.LendsumButton
import com.lendsumapp.lendsum.ui.components.LendsumField
import com.lendsumapp.lendsum.viewmodel.LoginViewModel

@Composable
fun ForgotPasswordScreen(
    navController: NavController
){
    val context = LocalContext.current
    val loginViewModel = hiltViewModel<LoginViewModel>()
    val forgotPassResponse = loginViewModel.resetPassState

    if (forgotPassResponse.status == Status.SUCCESS){
        LaunchedEffect(Unit){
            Toast.makeText(context, R.string.reset_email_sent, Toast.LENGTH_SHORT).show()
            navController.navigateUp()
        }
    }else{
        ForgotPasswordScreenContent(
            resetPassState = loginViewModel.resetPassState,
            onSendResetClicked = { email ->
                loginViewModel.sendPasswordResetEmail(context, email)
            },
            onBackButtonClicked = { navController.navigateUp() }
        )
    }
}

@Composable
fun ForgotPasswordScreenContent(
    resetPassState: Response<Unit>,
    onSendResetClicked:(String) -> Unit,
    onBackButtonClicked:() -> Unit
){
    var emailText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BackButton(color = Color.Black) {
            onBackButtonClicked.invoke()
        }
        LendsumField(
            keyBoardType = KeyboardType.Email,
            supportingLabel = stringResource(id = R.string.email),
            errorLabel = when(resetPassState.error){
                LendsumError.NO_INTERNET-> stringResource(id = R.string.not_connected_internet)
                LendsumError.INVALID_EMAIL -> stringResource(id = R.string.invalid_email_err_msg)
                else -> null
            },
            onTextChanged = {
                emailText = it
            }
        )
        LendsumButton(
            modifier = Modifier
                .fillMaxWidth(.60f)
                .align(Alignment.End),
            text = stringResource(id = R.string.send_password_reset_email)
        ) {
            onSendResetClicked(emailText)
        }
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun ForgotPasswordScreenPreview(){
    ForgotPasswordScreenContent(
        resetPassState = Response(),
        onSendResetClicked = {},
        onBackButtonClicked =  {}
    )
}

package com.lendsumapp.lendsum.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.ui.components.LendsumButton
import com.lendsumapp.lendsum.ui.components.LendsumField
import com.lendsumapp.lendsum.ui.components.noRippleClickable
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.NetworkUtils

@Composable
fun ForgotPasswordScreen(
    onBackButtonClicked:() -> Unit,
    onSendResetClicked: () -> Unit
){

    val context = LocalContext.current

    ForgotPasswordScreenContent(
        onSendResetClicked = {
            onSendResetClicked.invoke()
            Toast.makeText(context, R.string.reset_email_sent, Toast.LENGTH_SHORT).show()
        },
        onBackButtonClicked = onBackButtonClicked
    )
}

@Composable
fun ForgotPasswordScreenContent(
    onSendResetClicked:(String) -> Unit,
    onBackButtonClicked:() -> Unit
){

    val context = LocalContext.current
    var emailText by remember { mutableStateOf("") }
    var showEmailError by remember { mutableStateOf(false) }
    var showOnlineError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            modifier = Modifier.noRippleClickable {
                onBackButtonClicked.invoke()
            },
            painter = painterResource(id = R.drawable.ic_arrow_back_32),
            contentDescription = "Back Arrow", tint = Color.Black
        )
        LendsumField(
            keyBoardType = KeyboardType.Email,
            supportingLabel = stringResource(id = R.string.email),
            errorLabel = when {
                showOnlineError -> stringResource(id = R.string.not_connected_internet)
                showEmailError -> stringResource(id = R.string.invalid_email_err_msg)
                else -> ""
            },
            isError = showEmailError || showOnlineError,
            onTextChanged = {
                emailText = it
                showOnlineError = false
                showEmailError = false
            }
        )
        LendsumButton(
            modifier = Modifier
                .fillMaxWidth(.60f)
                .align(Alignment.End),
            text = stringResource(id = R.string.send_password_reset_email)
        ) {
            when{
                !NetworkUtils.isNetworkAvailable(context) -> showOnlineError = true
                !AndroidUtils.isValidEmail(emailText)-> showEmailError = true
                else -> onSendResetClicked(emailText)
            }
        }
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun ForgotPasswordScreenPreview(){
    ForgotPasswordScreenContent(
        onSendResetClicked = {},
        onBackButtonClicked =  {}
    )
}

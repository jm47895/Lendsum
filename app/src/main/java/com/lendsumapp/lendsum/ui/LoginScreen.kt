package com.lendsumapp.lendsum.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.ui.components.LendsumButton
import com.lendsumapp.lendsum.ui.components.LendsumClickableText
import com.lendsumapp.lendsum.ui.components.LendsumField
import com.lendsumapp.lendsum.ui.components.noRippleClickable
import com.lendsumapp.lendsum.ui.theme.ColorPrimary
import com.lendsumapp.lendsum.ui.theme.FacebookBlue
import com.lendsumapp.lendsum.ui.theme.GoogleFont
import com.lendsumapp.lendsum.ui.theme.GoogleLoginTextColor
import com.lendsumapp.lendsum.viewmodel.LoginViewModel

@Composable
fun LoginScreen(){

    val loginViewModel = hiltViewModel<LoginViewModel>()

    LoginScreenContent(
        onSignInClicked = { email, pass ->
            //loginViewModel.signInWithEmailAndPass(email, pass)
        },
        onForgotPasswordClicked = {

        },
        onSignUpWithEmailClicked = {

        },
        onContinueWithGoogleClicked = {

        },
        onContinueWithFacebookClicked = {

        }
    )
}

@Composable
fun LoginScreenContent(
    onSignInClicked: (String, String) -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onSignUpWithEmailClicked: () -> Unit,
    onContinueWithGoogleClicked: () -> Unit,
    onContinueWithFacebookClicked: () -> Unit

){

    val focusManager = LocalFocusManager.current
    var showSignUpOptions by remember { mutableStateOf(true) }
    var logInEmail by remember { mutableStateOf("") }
    var logInPass by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        LoginTitle()
        LendsumField(
            keyBoardType = KeyboardType.Email,
            supportingLabel = stringResource(id = R.string.email),
            onTextChanged = {
                logInEmail = it
            }
        )
        LendsumField(
            keyBoardType = KeyboardType.Password,
            supportingLabel = stringResource(id = R.string.password),
            onTextChanged = {
                logInPass = it
            }
        )


        LendsumButton(text = stringResource(id = R.string.sign_in).uppercase()) {
            onSignInClicked(logInEmail, logInPass)
        }

        LendsumClickableText(text = stringResource(id = R.string.forgot_password)) {
            onForgotPasswordClicked.invoke()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                thickness = 2.dp,
                color = Color.Black
            )
            LendsumClickableText(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = stringResource(id = R.string.create_account)
            ) {
                showSignUpOptions = !showSignUpOptions
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                thickness = 2.dp,
                color = Color.Black
            )
        }

        if(showSignUpOptions){
            SignUpOptions(
                onSignUpWithEmailClicked = onSignUpWithEmailClicked,
                onContinueWithGoogleClicked = onContinueWithGoogleClicked,
                onContinueWithFacebookClicked = onContinueWithFacebookClicked
            )
        }
    }
}

@Composable
fun SignUpOptions(
    onSignUpWithEmailClicked: () -> Unit,
    onContinueWithGoogleClicked: () -> Unit,
    onContinueWithFacebookClicked: () -> Unit
){
    LendsumButton(
        text = stringResource(id = R.string.sign_up_with_email).uppercase()
    ) {
        onSignUpWithEmailClicked.invoke()
    }
    Spacer(modifier = Modifier.height(4.dp))
    ThirdPartyLogInButton(
        drawable = R.drawable.googleg_standard_color_18,
        text = stringResource(id = R.string.continue_with_google),
        textColor = GoogleLoginTextColor,
        backgroundColor = Color.White
    ){
        onContinueWithGoogleClicked.invoke()
    }
    Spacer(modifier = Modifier.height(8.dp))
    ThirdPartyLogInButton(
        drawable = R.drawable.facebook_logo_58,
        text = stringResource(id = R.string.continue_with_facebook),
        textColor = Color.White,
        backgroundColor = FacebookBlue
    ){
        onContinueWithFacebookClicked.invoke()
    }

}

@Composable
fun ThirdPartyLogInButton(
    @DrawableRes drawable: Int,
    text: String,
    textColor: Color,
    backgroundColor: Color,
    onLoginClicked:() -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(100))
            .background(color = backgroundColor, shape = RoundedCornerShape(100))
            .noRippleClickable {
                onLoginClicked.invoke()
            }
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .weight(1f),
            painter = painterResource(
                id = drawable
            ),
            contentDescription = "",
            tint = Color.Unspecified
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f),
            text = text.uppercase(),
            textAlign = TextAlign.Center,
            color = textColor,
            fontFamily = GoogleFont,
        )
    }
}

@Composable
fun LoginTitle() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(.15f)
        .background(color = ColorPrimary),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "lendsum".uppercase(),
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun LoginScreenPreview(){
    LoginScreenContent(
        onSignInClicked = { _, _ ->},
        onForgotPasswordClicked = {},
        onSignUpWithEmailClicked = {},
        onContinueWithGoogleClicked = {},
        onContinueWithFacebookClicked = {}
    )
}
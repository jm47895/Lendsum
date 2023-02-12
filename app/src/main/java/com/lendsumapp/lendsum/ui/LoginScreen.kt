package com.lendsumapp.lendsum.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.lendsumapp.lendsum.data.model.Error
import com.lendsumapp.lendsum.data.model.Resource
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.ui.components.*
import com.lendsumapp.lendsum.ui.theme.ColorPrimary
import com.lendsumapp.lendsum.ui.theme.FacebookBlue
import com.lendsumapp.lendsum.ui.theme.GoogleLoginTextColor
import com.lendsumapp.lendsum.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
){

    val loginViewModel = hiltViewModel<LoginViewModel>()
    val context = LocalContext.current

    if(loginViewModel.loginState.status == Status.SUCCESS /*|| loginViewModel.firebaseUser != null*/){
        LaunchedEffect(Unit){
            navController.navigate(NavDestination.HOME.key){
                popUpTo(NavDestination.LOGIN.key){
                    inclusive = true
                }
            }
        }
    }else{
        LoginScreenContent(
            loginState = loginViewModel.loginState,
            onSignInClicked = { email, pass ->
                loginViewModel.signInWithEmailAndPass(context, email, pass)
            },
            onForgotPasswordClicked = { navController.navigate(NavDestination.PASSWORD_RESET.key) },
            onSignUpWithEmailClicked = {

            },
            onContinueWithGoogleClicked = {

            },
            onContinueWithFacebookClicked = {

            }
        )
    }
}

@Composable
fun LoginScreenContent(
    loginState: Resource<Unit>,
    onSignInClicked: (String, String) -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onSignUpWithEmailClicked: () -> Unit,
    onContinueWithGoogleClicked: () -> Unit,
    onContinueWithFacebookClicked: () -> Unit
){
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var showSignUpOptions by remember { mutableStateOf(true) }
    var logInEmail by remember { mutableStateOf("") }
    var logInPass by remember { mutableStateOf("") }

    when(loginState.status){
        Status.LOADING -> { LoadingAnimation() }
        Status.ERROR -> {
            when(loginState.error){
                Error.NO_INTERNET -> Toast.makeText(context, R.string.not_connected_internet, Toast.LENGTH_SHORT).show()
                Error.INVALID_LOGIN -> Toast.makeText(context, R.string.email_or_pass_wrong, Toast.LENGTH_SHORT).show()
                else ->{}
            }
        }
        Status.SUCCESS -> { /*Handled in top level screen for nav purposes*/ }
        null -> { /*Default status*/ }
    }

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
            onSignInClicked(logInEmail.trim(), logInPass)
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
        loginState = Resource(),
        onSignInClicked = { _, _ ->},
        onForgotPasswordClicked = {},
        onSignUpWithEmailClicked = {},
        onContinueWithGoogleClicked = {},
        onContinueWithFacebookClicked = {}
    )
}
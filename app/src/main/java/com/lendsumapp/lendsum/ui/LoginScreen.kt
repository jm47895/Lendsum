package com.lendsumapp.lendsum.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.ui.components.*
import com.lendsumapp.lendsum.ui.theme.ColorPrimary
import com.lendsumapp.lendsum.ui.theme.GoogleLoginTextColor
import com.lendsumapp.lendsum.util.NetworkUtils
import com.lendsumapp.lendsum.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
){

    val loginViewModel = hiltViewModel<LoginViewModel>()
    val context = LocalContext.current
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            result.data?.let { loginViewModel.handleGoogleSignInIntent(it) }
        }
    )
    
    if(loginViewModel.loginState.error == LendsumError.NO_INTERNET){
        Toast.makeText(context, context.getString(R.string.not_connected_internet).uppercase() , Toast.LENGTH_SHORT).show()
            .also { 
                loginViewModel.resetLoginState() 
                loginViewModel.resetGoogleSigInState()
            }
    }

    if(loginViewModel.googleSignInState.status == Status.SUCCESS){
        loginViewModel.resetGoogleSigInState()
        navController.navigate(NavDestination.CREATE_ACCOUNT.key)
    }

    if(loginViewModel.loginState.status == Status.SUCCESS || loginViewModel.firebaseUser != null){
        loginViewModel.resetLoginState()
        navController.navigate(NavDestination.HOME.key){
            popUpTo(NavDestination.LOGIN.key){
                inclusive = true
            }
        }
    }

    LoginScreenContent(
        loginState = loginViewModel.loginState,
        googleSignInState = loginViewModel.googleSignInState,
        onSignInClicked = { email, pass ->
            loginViewModel.signInWithEmailAndPass(email, pass)
        },
        onForgotPasswordClicked = { navController.navigate(NavDestination.PASSWORD_RESET.key) },
        onSignUpWithEmailClicked = { navController.navigate(NavDestination.CREATE_ACCOUNT.key) },
        onContinueWithGoogleClicked = {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()

            val intent = GoogleSignIn.getClient(context, options)
            googleSignInLauncher.launch(intent.signInIntent)
        },
    )

}

@Composable
fun LoginScreenContent(
    loginState: Response<Unit>,
    googleSignInState: Response<Unit>,
    onSignInClicked: (String, String) -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onSignUpWithEmailClicked: () -> Unit,
    onContinueWithGoogleClicked: () -> Unit
){
    val focusManager = LocalFocusManager.current
    var showSignUpOptions by remember { mutableStateOf(false) }
    var logInEmail by remember { mutableStateOf("") }
    var logInPass by remember { mutableStateOf("") }

    if(loginState.status == Status.LOADING || googleSignInState.status == Status.LOADING){
        LoadingAnimation()
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
            errorLabel = if (loginState.error == LendsumError.INVALID_LOGIN) stringResource(id = R.string.email_or_pass_wrong) else null,
            onTextChanged = {
                logInEmail = it
            }
        )
        LendsumField(
            keyBoardType = KeyboardType.Password,
            supportingLabel = stringResource(id = R.string.password),
            errorLabel = if (loginState.error == LendsumError.INVALID_LOGIN) stringResource(id = R.string.email_or_pass_wrong) else null,
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
                onContinueWithGoogleClicked = onContinueWithGoogleClicked
            )
        }
    }
}

@Composable
fun SignUpOptions(
    onSignUpWithEmailClicked: () -> Unit,
    onContinueWithGoogleClicked: () -> Unit
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
        loginState = Response(),
        googleSignInState = Response(),
        onSignInClicked = { _, _ ->},
        onForgotPasswordClicked = {},
        onSignUpWithEmailClicked = {},
        onContinueWithGoogleClicked = {}
    )
}
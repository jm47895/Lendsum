package com.lendsumapp.lendsum.ui

import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import com.lendsumapp.lendsum.data.model.AccountForm
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.ui.components.BackButton
import com.lendsumapp.lendsum.ui.components.LendsumButton
import com.lendsumapp.lendsum.ui.components.LendsumField
import com.lendsumapp.lendsum.ui.components.LoadingAnimation
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.viewmodel.CreateAccountViewModel

@Composable
fun CreateAccountScreen(
    navController: NavController
){
    val createAccountViewModel = hiltViewModel<CreateAccountViewModel>()
    val firebaseUser = createAccountViewModel.firebaseUser
    var accountForm: AccountForm? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    if(createAccountViewModel.createAccountState.error == LendsumError.NO_INTERNET){
        Toast.makeText(context, context.getString(R.string.not_connected_internet).uppercase(), Toast.LENGTH_SHORT).show().also { createAccountViewModel.resetCreateAccountState() }
    }

    if(createAccountViewModel.createAccountState.status == Status.SUCCESS){
        accountForm?.let{ createAccountViewModel.updateFirebaseAuthProfile(GlobalConstants.FIREBASE_PROFILE_NAME_KEY, "${it.firstName} ${it.lastName}") }
        createAccountViewModel.resetCreateAccountState()
        navController.navigate(NavDestination.NUMBER_VERIFICATION.key)
    }

    CreateAccountContent(
        pulledName = firebaseUser?.displayName?.let { createAccountViewModel.splitName(it) },
        pulledEmail = firebaseUser?.email,
        createAccountStatus = createAccountViewModel.createAccountState,
        onBackButtonPressed = {
            createAccountViewModel.logOut()
            navController.navigateUp()
        },
        onNextPressed = {
            if(createAccountViewModel.isValidAccountForm(it.firstName, it.lastName, it.email, it.pass, it.matchPass)){
                accountForm = it
                createAccountViewModel.createUserAccount(it.email, it.pass)
            }
        },
        onFieldChanged = { createAccountViewModel.resetCreateAccountState() }
    )
}

@Composable
fun CreateAccountContent(
    pulledName: Pair<String, String>?,
    pulledEmail: String?,
    createAccountStatus: Response<Unit>,
    onBackButtonPressed:() -> Unit,
    onNextPressed:(AccountForm) -> Unit,
    onFieldChanged:() -> Unit
){

    val focusManager = LocalFocusManager.current
    var firstName by remember { mutableStateOf(pulledName?.first ?:" ") }
    var lastName by remember { mutableStateOf( pulledName?.second ?:" ")}
    var email by remember { mutableStateOf(pulledEmail ?:" ") }
    var password by remember { mutableStateOf("") }
    var matchPassword by remember { mutableStateOf("") }

    if (createAccountStatus.status == Status.LOADING){
        LoadingAnimation()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        }
        .padding(8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        BackButton(
            color = Color.Black
        ) {
            onBackButtonPressed.invoke()
        }
        LendsumField(
            keyBoardType = KeyboardType.Text,
            defaultValue = pulledName?.first,
            supportingLabel = stringResource(id = R.string.first_name),
            supportingLabelColor = Color.Black,
            textColor = Color.Black,
            errorLabel = if (createAccountStatus.error == LendsumError.EMPTY_FIRST_NAME) stringResource(id = R.string.first_name_error_msg) else null,
            onTextChanged ={
                onFieldChanged.invoke()
                firstName = it
            }
        )
        LendsumField(
            keyBoardType = KeyboardType.Text,
            defaultValue = pulledName?.second,
            supportingLabel = stringResource(id = R.string.last_name),
            supportingLabelColor = Color.Black,
            textColor = Color.Black,
            errorLabel = if(createAccountStatus.error == LendsumError.EMPTY_LAST_NAME) stringResource(id = R.string.last_name_err_msg) else null,
            onTextChanged ={
                onFieldChanged.invoke()
                lastName = it }
        )
        LendsumField(
            keyBoardType = KeyboardType.Email,
            defaultValue = pulledEmail,
            supportingLabel = stringResource(id = R.string.email),
            supportingLabelColor = Color.Black,
            textColor = Color.Black,
            errorLabel = when (createAccountStatus.error) {
                LendsumError.INVALID_EMAIL -> stringResource(id = R.string.invalid_email_err_msg)
                LendsumError.USER_EMAIL_ALREADY_EXISTS -> stringResource(id = R.string.account_already_exists)
                LendsumError.LINK_ALREADY_EXISTS -> stringResource(id = R.string.account_already_exists)
                else -> null
            },
            onTextChanged ={
                onFieldChanged.invoke()
                email = it }
        )
        LendsumField(
            keyBoardType = KeyboardType.Password,
            supportingLabel = stringResource(id = R.string.password),
            supportingLabelColor = Color.Black,
            textColor = Color.Black,
            errorLabel = if(createAccountStatus.error == LendsumError.INVALID_PASS) stringResource(id = R.string.password_param_err_msg) else null,
            onTextChanged ={
                onFieldChanged.invoke()
                password = it }
        )
        LendsumField(
            keyBoardType = KeyboardType.Password,
            supportingLabel = stringResource(id = R.string.re_enter_password),
            supportingLabelColor = Color.Black,
            textColor = Color.Black,
            errorLabel = if(createAccountStatus.error == LendsumError.PASS_NO_MATCH) stringResource(id = R.string.pass_dont_match_err_msg) else null,
            onTextChanged ={
                onFieldChanged.invoke()
                matchPassword = it}
        )
        LendsumButton(
            modifier = Modifier
                .fillMaxWidth(.30f)
                .align(Alignment.End),
            text = stringResource(id = R.string.next)
        ) {
            onNextPressed(AccountForm(firstName.trim(), lastName.trim(), email.trim(), password.trim(), matchPassword.trim()))
        }
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun CreateAccountContentPreview(){
    /*CreateAccountContent(
        onBackButtonPressed = {}
    )*/
}
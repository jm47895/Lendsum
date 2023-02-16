package com.lendsumapp.lendsum.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.lendsumapp.lendsum.ui.components.BackButton
import com.lendsumapp.lendsum.ui.components.LendsumButton
import com.lendsumapp.lendsum.ui.components.LendsumField
import com.lendsumapp.lendsum.viewmodel.CreateAccountViewModel

@Composable
fun CreateAccountScreen(
    navController: NavController
){
    val createAccountViewModel = hiltViewModel<CreateAccountViewModel>()

    CreateAccountContent(
        createAccountError = createAccountViewModel.createAccountError,
        onBackButtonPressed = { navController.navigateUp() },
        onNextPressed = {
            if(createAccountViewModel.isValidAccountForm(it.firstName, it.lastName, it.email, it.pass, it.matchPass)){
                Log.d("ASDF", "account created")
            }else{
                Log.d("ASDF", "something is wrong")
            }
        }
    )
}

@Composable
fun CreateAccountContent(
    createAccountError: LendsumError?,
    onBackButtonPressed:() -> Unit,
    onNextPressed:(AccountForm) -> Unit
){

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var matchPassword by remember { mutableStateOf("") }

    Column {
        BackButton(color = Color.Black) {
            onBackButtonPressed.invoke()
        }
        LendsumField(
            modifier = Modifier.padding(vertical = 4.dp),
            keyBoardType = KeyboardType.Text,
            supportingLabel = stringResource(id = R.string.first_name),
            errorLabel = if (createAccountError == LendsumError.EMPTY_FIRST_NAME) stringResource(id = R.string.first_name_error_msg) else null,
            onTextChanged ={ firstName = it}
        )
        LendsumField(
            modifier = Modifier.padding(vertical = 4.dp),
            keyBoardType = KeyboardType.Text,
            supportingLabel = stringResource(id = R.string.last_name),
            errorLabel = if(createAccountError == LendsumError.EMPTY_LAST_NAME) stringResource(id = R.string.last_name_err_msg) else null,
            onTextChanged ={ lastName = it }
        )
        LendsumField(
            modifier = Modifier.padding(vertical = 4.dp),
            keyBoardType = KeyboardType.Email,
            supportingLabel = stringResource(id = R.string.email),
            errorLabel = if(createAccountError == LendsumError.INVALID_EMAIL) stringResource(id = R.string.invalid_email_err_msg) else null,
            onTextChanged ={ email = it }
        )
        LendsumField(
            modifier = Modifier.padding(vertical = 4.dp),
            keyBoardType = KeyboardType.Password,
            supportingLabel = stringResource(id = R.string.password),
            errorLabel = if(createAccountError == LendsumError.INVALID_PASS) stringResource(id = R.string.password_param_err_msg) else null,
            onTextChanged ={ password = it }
        )
        LendsumField(
            modifier = Modifier.padding(vertical = 4.dp),
            keyBoardType = KeyboardType.Password,
            supportingLabel = stringResource(id = R.string.re_enter_password),
            errorLabel = if(createAccountError == LendsumError.PASS_NO_MATCH) stringResource(id = R.string.pass_dont_match_err_msg) else null,
            onTextChanged ={ matchPassword = it}
        )
        LendsumButton(
            modifier = Modifier
                .fillMaxWidth(.30f)
                .align(Alignment.End),
            text = stringResource(id = R.string.next)
        ) {
            onNextPressed(AccountForm(firstName, lastName, email, password, matchPassword))
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
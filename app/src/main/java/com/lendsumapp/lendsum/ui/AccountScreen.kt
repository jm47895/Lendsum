package com.lendsumapp.lendsum.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.ui.components.BackButton
import com.lendsumapp.lendsum.ui.components.LendsumButton
import com.lendsumapp.lendsum.ui.components.LendsumField
import com.lendsumapp.lendsum.ui.components.LoadingAnimation
import com.lendsumapp.lendsum.ui.theme.ColorPrimary
import com.lendsumapp.lendsum.viewmodel.AccountViewModel

@Composable
fun AccountScreen(
    navController: NavController
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val accountViewModel = hiltViewModel<AccountViewModel>()
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                accountViewModel.uploadProfilePhoto(context, lifecycleOwner, uri)
                accountViewModel.updateProfile(context, lifecycleOwner, profilePicUri = it.toString())
            }
        }
    )

    AccountScreenContent(
        user = accountViewModel.currentUser.value,
        updateProfileState = accountViewModel.updateProfileState.value,
        updateEmailState = accountViewModel.updateEmailState.value,
        updatePassState = accountViewModel.updatePassState.value,
        onBackClicked = {
            navController.navigateUp()
        },
        onChangeProfilePic = {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onSaveProfileInfo = { name, username ->
            accountViewModel.updateProfile(context, lifecycleOwner, name = name, username = username)
        },
        onUpdateEmail = { email ->
            accountViewModel.updateAuthEmail(context, email)
        },
        onUpdatePass = { password, matchPass ->
            accountViewModel.updateAuthPass(context, password, matchPass)
        },
        resetUpdateProfileState = {
            accountViewModel.resetUpdateProfileState()
        },
        resetUpdateEmailState = {
            accountViewModel.resetUpdateEmailState()
        },
        resetUpdatePassState = {
            accountViewModel.resetUpdatePassState()
        }
    )
}

@Composable
fun AccountScreenContent(
    user: User?,
    updateProfileState: Response<Unit>,
    updateEmailState: Response<Unit>,
    updatePassState: Response<Unit>,
    onBackClicked:() -> Unit,
    onChangeProfilePic: () -> Unit,
    onSaveProfileInfo: (String, String) -> Unit,
    onUpdateEmail: (String) -> Unit,
    onUpdatePass: (String, String) -> Unit,
    resetUpdateProfileState: () -> Unit,
    resetUpdateEmailState:() -> Unit,
    resetUpdatePassState:() -> Unit
) {

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var matchPass by remember { mutableStateOf("") }

    user?.let {
        name = it.name
        username = it.username
    }

    LaunchedEffect(updateProfileState.status){
        when(updateProfileState.status){
            Status.SUCCESS -> {
                Toast.makeText(context, context.getString(R.string.user_profile_updated), Toast.LENGTH_SHORT).show()
            }
            Status.ERROR -> {
                when(updateProfileState.error){
                    LendsumError.OFFLINE_MODE -> Toast.makeText(context, context.getString(R.string.offline_edit_msg), Toast.LENGTH_SHORT).show()
                    LendsumError.FAILED_TO_UPDATE_PROFILE -> Toast.makeText(context, context.getString(R.string.profile_update_err), Toast.LENGTH_SHORT).show()
                    else->{}
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(updateEmailState.status){
        when(updateEmailState.status){
            Status.SUCCESS -> {
                Toast.makeText(context, context.getString(R.string.user_profile_updated), Toast.LENGTH_SHORT).show()
            }
            Status.ERROR -> {
                when(updateEmailState.error){
                    LendsumError.LOGIN_REQUIRED -> Toast.makeText(context, context.getString(R.string.sign_in_again_msg), Toast.LENGTH_SHORT).show()
                    LendsumError.FAILED_TO_UPDATE_EMAIL -> Toast.makeText(context, context.getString(R.string.profile_update_err), Toast.LENGTH_SHORT).show()
                    else->{}
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(updatePassState.status){
        when(updatePassState.status){
            Status.SUCCESS -> {
                Toast.makeText(context, context.getString(R.string.password_has_updated), Toast.LENGTH_SHORT).show()
            }
            Status.ERROR -> {
                when(updatePassState.error){
                    LendsumError.LOGIN_REQUIRED -> Toast.makeText(context, context.getString(R.string.sign_in_again_msg), Toast.LENGTH_SHORT).show()
                    LendsumError.NO_INTERNET -> Toast.makeText(context, context.getString(R.string.internet_required), Toast.LENGTH_SHORT).show()
                    else->{}
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ){

        if(updateProfileState.status == Status.LOADING){
            LoadingAnimation()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .verticalScroll(scrollState)
                .padding(8.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            BackButton(color = Color.White) {
                onBackClicked.invoke()
            }
            EditProfPic(
                uri = user?.profilePicUri ?: "",
                onChangeProfilePic = onChangeProfilePic
            )
            LabeledDivider(label = stringResource(R.string.edit_profile).uppercase())
            user?.let {
                EditProfileOptions(
                    updateProfileState = updateProfileState,
                    user = user,
                    onNameChanged = {
                        name = it
                        resetUpdateProfileState.invoke()
                    },
                    onUsernameChanged = {
                        username = it
                        resetUpdateProfileState.invoke()
                    }
                )
            }
            LendsumButton(
                modifier = Modifier
                    .fillMaxWidth(.33f)
                    .align(Alignment.End),
                text = stringResource(id = R.string.save).uppercase()
            ) {
                onSaveProfileInfo(name, username)
            }
            LabeledDivider(label = stringResource(id = R.string.edit_login).uppercase())
            user?.let {
                EditCredentialOptions(
                    modifier = Modifier.align(Alignment.End),
                    updateEmailState = updateEmailState,
                    updatePassState = updatePassState,
                    user = user,
                    onEmailChanged = {
                        email = it
                        resetUpdateEmailState.invoke()
                    },
                    onPasswordChanged = {
                        password = it
                        resetUpdatePassState.invoke()
                    },
                    onMatchPassChanged = {
                        matchPass = it
                        resetUpdatePassState.invoke()
                    },
                    onEmailUpdated = {
                        onUpdateEmail(email.trim())
                    },
                    onPasswordUpdated = {
                        onUpdatePass(password.trim(), matchPass.trim())
                    }
                )
            }
        }
    }
}

@Composable
fun EditProfileOptions(
    updateProfileState: Response<Unit>,
    user: User,
    onNameChanged:(String) ->  Unit,
    onUsernameChanged:(String) ->  Unit,
) {

    EditProfileOptions.values().forEach { editOption ->

        LendsumField(
            modifier = Modifier.fillMaxWidth(),
            keyBoardType = editOption.keyboardType,
            supportingLabel = stringResource(editOption.supportingLabel),
            supportingLabelColor = Color.White,
            textColor = Color.White,
            errorLabel = when(editOption){
                EditProfileOptions.ACCOUNT_NAME -> if(updateProfileState.error == LendsumError.EMPTY_NAME) stringResource(id = R.string.empty_profile_name) else null
                EditProfileOptions.ACCOUNT_USERNAME -> if(updateProfileState.error == LendsumError.EMPTY_USERNAME) stringResource(id = R.string.empty_username) else null
            },
            defaultValue = when(editOption){
                EditProfileOptions.ACCOUNT_NAME -> user.name
                EditProfileOptions.ACCOUNT_USERNAME -> user.username
            },
            onTextChanged = {
                when(editOption){
                    EditProfileOptions.ACCOUNT_NAME -> onNameChanged(it)
                    EditProfileOptions.ACCOUNT_USERNAME ->  onUsernameChanged(it)
                }
            }
        )
    }
}

enum class EditProfileOptions(val keyboardType: KeyboardType, @StringRes val supportingLabel: Int){
    ACCOUNT_NAME(KeyboardType.Text, R.string.name),
    ACCOUNT_USERNAME(KeyboardType.Text, R.string.username)
}

@Composable
fun EditCredentialOptions(
    modifier: Modifier = Modifier,
    updateEmailState: Response<Unit>,
    updatePassState: Response<Unit>,
    user: User,
    onEmailChanged:(String) -> Unit,
    onPasswordChanged:(String) -> Unit,
    onMatchPassChanged:(String) -> Unit,
    onEmailUpdated:() -> Unit,
    onPasswordUpdated:() -> Unit
) {

    EditCredentialOptions.values().forEach { credentialOption ->

        LendsumField(
            modifier = Modifier.fillMaxWidth(),
            keyBoardType = credentialOption.keyboardType,
            supportingLabel = stringResource(credentialOption.supportingLabel),
            supportingLabelColor = Color.White,
            textColor = Color.White,
            errorLabel = when(credentialOption){
                EditCredentialOptions.ACCOUNT_EMAIL -> when(updateEmailState.error){
                    LendsumError.NO_INTERNET -> stringResource(id = R.string.not_connected_internet)
                    LendsumError.INVALID_EMAIL -> stringResource(id = R.string.invalid_email_err_msg)
                    else -> { null }
                }
                EditCredentialOptions.ACCOUNT_PASSWORD -> when(updatePassState.error){
                    LendsumError.INVALID_PASS -> stringResource(id = R.string.password_param_err_msg)
                    LendsumError.EMPTY_PASS -> stringResource(id = R.string.blank_pass_no_update)
                    else -> {null}
                }
                EditCredentialOptions.ACCOUNT_MATCH_PASSWORD -> null
            },
            defaultValue = when(credentialOption){
                EditCredentialOptions.ACCOUNT_EMAIL -> user.email
                else -> { null }
            },
            onTextChanged = {
                when(credentialOption){
                    EditCredentialOptions.ACCOUNT_EMAIL -> onEmailChanged(it)
                    EditCredentialOptions.ACCOUNT_PASSWORD -> onPasswordChanged(it)
                    EditCredentialOptions.ACCOUNT_MATCH_PASSWORD -> onMatchPassChanged(it)
                }
            }
        )

        if (credentialOption == EditCredentialOptions.ACCOUNT_EMAIL || credentialOption == EditCredentialOptions.ACCOUNT_MATCH_PASSWORD) {
            LendsumButton(
                modifier = modifier
                    .fillMaxWidth(.33f),
                text = stringResource(id = R.string.save).uppercase()
            ) {
                when(credentialOption){
                    EditCredentialOptions.ACCOUNT_EMAIL -> onEmailUpdated.invoke()
                    EditCredentialOptions.ACCOUNT_MATCH_PASSWORD -> onPasswordUpdated.invoke()
                    else -> {}
                }
            }
        }
    }
}

enum class EditCredentialOptions(val keyboardType: KeyboardType, @StringRes val supportingLabel: Int){
    ACCOUNT_EMAIL(KeyboardType.Text, R.string.email),
    ACCOUNT_PASSWORD(KeyboardType.Password, R.string.password),
    ACCOUNT_MATCH_PASSWORD(KeyboardType.Password, R.string.re_enter_password)
}

@Composable
fun LabeledDivider(
    label: String
) {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        thickness = 2.dp,
        color = ColorPrimary
    )
    Text(text = label, color = ColorPrimary)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EditProfPic(
    modifier: Modifier = Modifier,
    uri: String,
    onChangeProfilePic:() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        GlideImage(
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .width(175.dp)
                .height(175.dp),
            model = uri,
            contentDescription = "Profile Pic",
            contentScale = ContentScale.Inside,
        )
        Box(
            modifier = Modifier
                .clickable {
                    onChangeProfilePic.invoke()
                }
                .clip(RoundedCornerShape(100))
                .width(175.dp)
                .height(175.dp),
            contentAlignment = Alignment.BottomCenter
        ){
            Icon(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(100)
                    )
                    .padding(4.dp),
                painter = painterResource(id = R.drawable.ic_edit_24),
                contentDescription = "Edit symbol",
                tint = ColorPrimary
            )
        }
    }
}

@Preview
@Composable
fun AccountScreenPreview() {
    AccountScreenContent(
        user = User(),
        updateProfileState = Response(),
        updateEmailState = Response(),
        updatePassState = Response(),
        onBackClicked = {},
        onChangeProfilePic = {},
        onSaveProfileInfo = { name, username ->},
        onUpdatePass = {pass, matchPass ->},
        onUpdateEmail = {email ->},
        resetUpdateProfileState = {},
        resetUpdateEmailState = {},
        resetUpdatePassState = {}
    )
}
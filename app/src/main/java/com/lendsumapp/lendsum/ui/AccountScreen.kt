package com.lendsumapp.lendsum.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
                accountViewModel.uploadProfilePhoto(uri)
            }
        }
    )

    AccountScreenContent(
        user = accountViewModel.currentUser,
        updateProfileState = accountViewModel.updateProfileState,
        onBackClicked = {
            navController.navigateUp()
        },
        onChangeProfilePic = {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onSaveProfileInfo = { name, username ->
            accountViewModel.updateProfile(context, lifecycleOwner, name, username)
        },
        resetUpdateProfileState = {
            accountViewModel.resetUpdateProfileState()
        }
    )
}

@Composable
fun AccountScreenContent(
    user: User?,
    updateProfileState: Response<Unit>,
    onBackClicked:() -> Unit,
    onChangeProfilePic: () -> Unit,
    onSaveProfileInfo: (String, String) -> Unit,
    resetUpdateProfileState: () -> Unit,
) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

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
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                thickness = 2.dp,
                color = ColorPrimary
            )
            Text(text = stringResource(R.string.edit_profile).uppercase(), color = Color.White)
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
                    },
                )
            }
            LendsumButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.save)
            ) {
                onSaveProfileInfo(name, username)
            }
        }
    }
}

@Composable
fun EditProfileOptions(
    updateProfileState: Response<Unit>,
    user: User,
    onNameChanged:(String) ->  Unit,
    onUsernameChanged:(String) ->  Unit
) {

    EditProfileOption.values().forEach { editOption ->

        LendsumField(
            modifier = Modifier.fillMaxWidth(),
            keyBoardType = editOption.keyboardType,
            supportingLabel = stringResource(editOption.supportingLabel),
            supportingLabelColor = Color.White,
            textColor = Color.White,
            errorLabel = when(editOption){
                EditProfileOption.ACCOUNT_NAME -> if(updateProfileState.error == LendsumError.EMPTY_NAME) stringResource(id = R.string.empty_profile_name) else null
                EditProfileOption.ACCOUNT_USERNAME -> if(updateProfileState.error == LendsumError.EMPTY_USERNAME) stringResource(id = R.string.empty_username) else null
            },
            defaultValue = when(editOption){
                EditProfileOption.ACCOUNT_NAME -> user.name
                EditProfileOption.ACCOUNT_USERNAME -> user.username
            },
            onTextChanged = {

                when(editOption){
                    EditProfileOption.ACCOUNT_NAME -> onNameChanged(it)
                    EditProfileOption.ACCOUNT_USERNAME ->  onUsernameChanged(it)
                }
            }
        )

    }
}

enum class EditProfileOption(val keyboardType: KeyboardType, @StringRes val supportingLabel: Int){
    ACCOUNT_NAME(KeyboardType.Text, R.string.name),
    ACCOUNT_USERNAME(KeyboardType.Text, R.string.username)
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
            .fillMaxWidth()
            .fillMaxHeight(.25f),
        contentAlignment = Alignment.Center
    ) {
        GlideImage(
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .fillMaxWidth(.50f)
                .fillMaxHeight(),
            model = uri,
            contentDescription = "Profile Pic",
            contentScale = ContentScale.FillBounds,
        )
        Box(
            modifier = Modifier
                .clickable {
                    onChangeProfilePic.invoke()
                }
                .clip(RoundedCornerShape(100))
                .fillMaxWidth(.50f)
                .fillMaxHeight(),
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
        onBackClicked = {},
        onChangeProfilePic = {},
        onSaveProfileInfo = { name, username->},
        resetUpdateProfileState = {}
    )
}
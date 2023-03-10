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
        onSaveProfileInfo = {
            accountViewModel.updateProfile(lifecycleOwner,it)
        }
    )
}

@Composable
fun AccountScreenContent(
    user: User?,
    updateProfileState: Response<Unit>,
    onBackClicked:() -> Unit,
    onChangeProfilePic: () -> Unit,
    onSaveProfileInfo: (User) -> Unit
) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var updatedUser by remember { mutableStateOf(user) }

    when(updateProfileState.status){
        Status.LOADING -> {
            LoadingAnimation()
        }
        Status.SUCCESS -> {
            Toast.makeText(context, "Profile has been updated.", Toast.LENGTH_SHORT).show()
        }
        Status.ERROR -> {
            Toast.makeText(context, "There was an error editing the info please try again.", Toast.LENGTH_SHORT).show()
        }
        null -> {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ){
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
                EditOptions(
                    user = user,
                    onUserInfoChanged = {
                        updatedUser = it
                    }
                )
            }
            LendsumButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.save)
            ) {
                updatedUser?.let { onSaveProfileInfo(it) }
            }
        }
    }
}

@Composable
fun EditOptions(
    user: User,
    onUserInfoChanged:(User) ->  Unit
) {

    var name by remember { mutableStateOf(user.name) }
    var username by remember { mutableStateOf(user.username) }
    var email by remember { mutableStateOf(user.email) }

    EditOption.values().forEach { editOption ->

        LendsumField(
            modifier = Modifier.fillMaxWidth(),
            keyBoardType = editOption.keyboardType,
            supportingLabel = stringResource(editOption.supportingLabel),
            supportingLabelColor = Color.White,
            textColor = Color.White,
            defaultValue = when(editOption){
                EditOption.ACCOUNT_NAME -> user.name
                EditOption.ACCOUNT_USERNAME -> user.username
                EditOption.ACCOUNT_EMAIL -> user.email
            },
            onTextChanged = {
                when(editOption){
                    EditOption.ACCOUNT_NAME -> {
                        name = it
                    }
                    EditOption.ACCOUNT_USERNAME -> username = it
                    EditOption.ACCOUNT_EMAIL -> email = it
                }
                user.name = name
                user.username = username
                user.email = email
                onUserInfoChanged(user)
            }
        )

    }
}

enum class EditOption(val keyboardType: KeyboardType, @StringRes val supportingLabel: Int){
    ACCOUNT_NAME(KeyboardType.Text, R.string.name),
    ACCOUNT_USERNAME(KeyboardType.Text, R.string.username),
    ACCOUNT_EMAIL(KeyboardType.Email, R.string.email)
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
        onSaveProfileInfo = {}
    )
}
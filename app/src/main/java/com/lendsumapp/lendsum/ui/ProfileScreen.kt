package com.lendsumapp.lendsum.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.Bundle
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.ui.theme.ColorPrimary
import com.lendsumapp.lendsum.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onSettingsClicked: () -> Unit
) {

    val profileViewModel = hiltViewModel<ProfileViewModel>()

    ProfileContent(
        profileViewModel.user,
        onSettingsClicked = {
            onSettingsClicked.invoke()
        }
    )
}

@Composable
fun ProfileContent(
    user: User,
    onSettingsClicked: () -> Unit
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(.95f)
        .background(Color.Black)
    ){
        ProfilePic(
            modifier = Modifier.align(Alignment.TopStart),
            user = user
        )

        ProfileInfo(
            onSettingsClicked = onSettingsClicked,
            modifier = Modifier.align(Alignment.TopEnd),
            user = user
        )

        ProfileLists(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ProfileInfo(
    modifier: Modifier = Modifier,
    user: User,
    onSettingsClicked:() -> Unit
){
    Column(
        modifier = modifier
            .fillMaxWidth(.5f)
            .fillMaxHeight(.25f)
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ){
            Icon(painter = painterResource(id = R.drawable.ic_qr_code_scanner_32), contentDescription = "Settings Icon", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.clickable {
                    onSettingsClicked.invoke()
                },
                painter = painterResource(id = R.drawable.ic_settings_32), contentDescription = "Settings Icon", tint = Color.White
            )
        }
        Text(style = MaterialTheme.typography.bodyLarge, text = user.name, color = Color.White, fontWeight = FontWeight.Bold)
        Text(style = MaterialTheme.typography.bodyLarge, text = user.username, color = Color.White)
        Text(style = MaterialTheme.typography.bodyLarge, text = stringResource(id = R.string.karma_score) + user.karmaScore, color = Color.White)
    }
}

@Composable
fun ProfileLists(
    modifier: Modifier = Modifier
){

    var selectedTab by remember { mutableStateOf(ProfileListOptions.FRIENDS) }

    val list = when(selectedTab){
        ProfileListOptions.BUNDLES -> listOf(Bundle(bundleTitle = "Bundle 1"), Bundle(bundleTitle = "Bundle 2"), Bundle(bundleTitle = "Bundle 3"), Bundle(bundleTitle = "Bundle 4"),)
        ProfileListOptions.FRIENDS -> listOf("Jordan", "Lesly", "Louis", "Brad", "Paul", "Stephanie")
        ProfileListOptions.SERVICES -> listOf("Lawn mow", "Rent car", "Look after house", "Walk the dog")
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(.75f)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(.08f)
                .background(ColorPrimary),
        ) {
            ProfileListOptions.values().forEach {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .clickable {
                            selectedTab = it
                        }
                        .border(1.dp, color = Color.White)
                        .background(color = if (it == selectedTab) Color.White else ColorPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = it.name.uppercase(), color = if (it == selectedTab) ColorPrimary else Color.White)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ){
            items(list) { item ->
                when(item){
                    is Bundle -> Text(modifier = Modifier.padding(8.dp), text = item.bundleTitle, color = Color.White)
                    is String -> Text(modifier = Modifier.padding(8.dp), text = item, color = Color.White)
                }

            }
        }
    }
}

enum class ProfileListOptions(val labelId: Int){
    BUNDLES(R.string.cap_bundles),
    FRIENDS(R.string.friends),
    SERVICES(R.string.cap_services)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfilePic(
    modifier: Modifier = Modifier,
    user: User
){
    Box(modifier = modifier
        .fillMaxWidth(.5f)
        .fillMaxHeight(.25f)
        .padding(20.dp),
        contentAlignment = Alignment.Center
    ){
        GlideImage(
            modifier = Modifier.clip(RoundedCornerShape(100)),
            model = user.profilePicUri,
            contentDescription = "Profile Pic",
            contentScale = ContentScale.FillBounds
        )
    }
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun ProfileContentPreview() {
    ProfileContent(
        user = User(),
        onSettingsClicked = {}
    )
}
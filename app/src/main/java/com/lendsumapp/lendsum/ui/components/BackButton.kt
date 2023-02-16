package com.lendsumapp.lendsum.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.lendsumapp.lendsum.R

@Composable
fun BackButton(
    color: Color,
    onBackButtonClicked:() -> Unit
){
    Icon(
        modifier = Modifier.noRippleClickable {
            onBackButtonClicked.invoke()
        },
        painter = painterResource(id = R.drawable.ic_arrow_back_32),
        contentDescription = "Back Arrow", tint = color
    )
}
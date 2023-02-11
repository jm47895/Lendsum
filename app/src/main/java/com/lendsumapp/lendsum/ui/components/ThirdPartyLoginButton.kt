package com.lendsumapp.lendsum.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lendsumapp.lendsum.ui.theme.GoogleFont

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
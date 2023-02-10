package com.lendsumapp.lendsum.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.lendsumapp.lendsum.ui.theme.ColorPrimary

@Composable
fun LendsumClickableText(
    modifier: Modifier = Modifier,
    text:String,
    onTextClicked:() -> Unit
){
    Text(
        modifier = modifier
            .padding(8.dp)
            .noRippleClickable {
                onTextClicked.invoke()
            },
        color = ColorPrimary,
        fontWeight = FontWeight.Bold ,
        text = text,
        textAlign = TextAlign.Center,
        style = TextStyle(textDecoration = TextDecoration.Underline)
    )
}
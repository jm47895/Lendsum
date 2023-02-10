package com.lendsumapp.lendsum.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lendsumapp.lendsum.ui.theme.ColorPrimary

@Composable
fun LendsumButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
){
    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        onClick = { onClick.invoke()},
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorPrimary
        )
    ) {

        Text(text = text, color = Color.White)
    }
}
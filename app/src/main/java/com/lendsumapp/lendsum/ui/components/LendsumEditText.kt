package com.lendsumapp.lendsum.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.lendsumapp.lendsum.ui.theme.ColorPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LendsumField(
    modifier: Modifier = Modifier,
    defaultValue: String? = null,
    keyBoardType: KeyboardType,
    supportingLabel: String,
    errorLabel: String? = null,
    onTextChanged :(String) -> Unit,
){

    var input by remember { mutableStateOf(defaultValue?:"") }

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        value = input,
        onValueChange = {
            input = it
            onTextChanged(input)
        },
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = Color.Black,
            focusedIndicatorColor = ColorPrimary,
            unfocusedIndicatorColor = Color.Black
        ),
        supportingText = {
            Text(text = errorLabel?.let{ errorLabel } ?: supportingLabel, color = errorLabel?.let{ Color.Red } ?: Color.Black)
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyBoardType),
        visualTransformation =
        if (keyBoardType == KeyboardType.Password)
            PasswordVisualTransformation()
        else
            VisualTransformation.None
    )

}
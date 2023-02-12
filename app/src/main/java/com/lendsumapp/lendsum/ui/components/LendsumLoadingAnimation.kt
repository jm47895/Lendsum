package com.lendsumapp.lendsum.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lendsumapp.lendsum.ui.theme.ColorPrimary

@Composable
fun LoadingAnimation(
    modifier: Modifier = Modifier
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator(
            modifier = modifier,
            color = ColorPrimary
        )
    }
}
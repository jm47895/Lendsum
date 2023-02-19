package com.lendsumapp.lendsum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.ui.theme.ColorPrimary
import com.lendsumapp.lendsum.ui.theme.LightGray12

@Composable
fun CountryCodePicker(
    modifier: Modifier = Modifier,
    defaultLabel: String?,
    expandDropdown: Boolean,
    countryCodeList: List<CountryCode>,
    onCountryCodeChanged:(CountryCode) -> Unit,
    onDropDownToggled:() -> Unit
){

    var label by remember { mutableStateOf(defaultLabel ?: "") }
    
    Column(
        modifier = Modifier.padding(top = 8.dp)
    ){
        Row(
            modifier = modifier
                .height(50.dp)
                .width(100.dp)
                .noRippleClickable {
                    onDropDownToggled.invoke()
                }
                .background(ColorPrimary),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Icon(
                painter = painterResource(id = if(expandDropdown) R.drawable.ic_dropdown_collapse_24 else R.drawable.ic_dropdown_expand_24),
                contentDescription = "Expand list button", 
                tint = Color.White
            )

            Text(text = label, color = Color.White)
            
        }
        Spacer(modifier = Modifier.height(4.dp))
        if(expandDropdown){
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(.25f)
                    .width(100.dp)
            ){
                items(countryCodeList){
                    Box(
                        modifier
                            .fillMaxWidth()
                            .clickable {
                                onCountryCodeChanged(it)
                                label = "${it.name} ${it.countryCode}"
                            }
                            .background(LightGray12)
                        ,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(modifier = Modifier.padding(vertical = 8.dp), text = "${it.name} ${it.countryCode}")
                    }
                }
            }
        }
    }
}

enum class CountryCode(val countryCode: String){
    US("+1"),
    CA("+1"),
    MX("+52"),
}
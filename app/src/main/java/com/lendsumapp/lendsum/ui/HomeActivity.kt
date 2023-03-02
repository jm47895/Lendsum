package com.lendsumapp.lendsum.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        setContent {
            Surface {
                LendsumNavHost()
            }
        }

    }
}
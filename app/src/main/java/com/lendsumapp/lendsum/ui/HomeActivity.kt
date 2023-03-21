package com.lendsumapp.lendsum.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.util.NetworkConnectivity
import com.lendsumapp.lendsum.util.NetworkObserver
import com.lendsumapp.lendsum.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity: AppCompatActivity(){

    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        homeViewModel.observeNetwork()

        setContent {
            Surface {
                LaunchedEffect(homeViewModel.networkState.value){
                    when (homeViewModel.networkState.value) {
                        NetworkObserver.NetworkState.AVAILABLE -> {
                            Toast.makeText(this@HomeActivity, resources.getText(R.string.connected_internet), Toast.LENGTH_SHORT).show()
                        }
                        NetworkObserver.NetworkState.LOST -> {
                            Toast.makeText(this@HomeActivity, resources.getText(R.string.not_connected_internet), Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
                LendsumNavHost()
            }
        }

    }
}
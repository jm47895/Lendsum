package com.lendsumapp.lendsum.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lendsumapp.lendsum.util.NetworkConnectivity
import com.lendsumapp.lendsum.util.NetworkObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkConnectivity: NetworkConnectivity
): ViewModel() {

    private val _networkState = mutableStateOf(NetworkObserver.NetworkState.UNAVAILABLE)

    val networkState: NetworkObserver.NetworkState
        get() = _networkState.value

    fun observeNetwork(){
        viewModelScope.launch{
            networkConnectivity.observe().collect{
                _networkState.value = it
            }
        }
    }

    companion object{
        private val TAG = HomeViewModel::class.java.simpleName
    }
}
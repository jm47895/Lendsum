package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lendsumapp.lendsum.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
): ViewModel() {
    fun logOut(){
        viewModelScope.launch {
            loginRepository.logOut()
        }
    }
}
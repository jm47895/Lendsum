package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lendsumapp.lendsum.repository.LoginRepository
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    @ActivityContext private var context: Context
): ViewModel(){

    fun logOutOfGoogle(){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.logOutOfGoogle()
        }
    }

    fun configureGoogleAuth(){
        loginRepository.configureGoogleAuth(context)
    }

    fun logOutOfEmailAndPass(){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.logOutOfEmailAndPass()
        }
    }

    fun logOutOfFacebook(){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.logOutOfFacebook()
        }
    }

}
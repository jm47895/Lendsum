package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.lendsumapp.lendsum.repository.LoginRepository

class ProfileViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository
): ViewModel(){

    fun logOutOfGoogle(){
        loginRepository.logOutOfGoogle()
    }

    fun configureGoogleAuth(){
        loginRepository.configureGoogleAuth()
    }

    fun logOutOfEmailAndPass(){
        loginRepository.logOutOfEmailAndPass()
    }

}
package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.lendsumapp.lendsum.repository.LoginRepository
import dagger.hilt.android.qualifiers.ActivityContext

class NumberVerificationViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    @ActivityContext private var context: Context
): ViewModel(){

    fun logOutOfGoogle(){
        loginRepository.logOutOfGoogle()
    }

    fun configureGoogleAuth(){
        loginRepository.configureGoogleAuth(context)
    }

    fun logOutOfFacebook(){
        loginRepository.logOutOfFacebook()
    }
}
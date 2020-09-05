package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lendsumapp.lendsum.repository.LoginRepository

class CreateAccountViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository
): ViewModel(){

    fun createUserAccount(email: String, password: String){
        loginRepository.registerWithEmailAndPassword(email, password)
    }

    fun getEmailSignUpStatus(): MutableLiveData<Boolean> {
        return loginRepository.getEmailSignUpStatus()
    }
}
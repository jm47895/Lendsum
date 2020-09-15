package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lendsumapp.lendsum.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateAccountViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository
): ViewModel(){

    fun createUserAccount(email: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.registerWithEmailAndPassword(email, password)
        }
    }

    fun getEmailSignUpStatus(): MutableLiveData<Boolean> {
        return loginRepository.getEmailSignUpStatus()
    }
}
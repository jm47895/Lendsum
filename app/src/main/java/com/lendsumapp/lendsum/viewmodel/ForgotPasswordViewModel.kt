package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lendsumapp.lendsum.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository
):ViewModel() {

    fun sendPasswordResetEmail(email: String){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.sendPasswordResetEmail(email)
        }
    }

    fun getResetEmailStatus(): MutableLiveData<Boolean> {
        return loginRepository.getResetEmailStatus()
    }
}
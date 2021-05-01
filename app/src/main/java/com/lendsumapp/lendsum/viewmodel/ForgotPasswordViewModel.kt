package com.lendsumapp.lendsum.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lendsumapp.lendsum.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val loginRepository: LoginRepository
):ViewModel() {

    fun sendPasswordResetEmail(email: String){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.sendPasswordResetEmail(email)
        }
    }

    fun getResetPasswordEmailStatus(): MutableLiveData<Boolean> {
        return loginRepository.getResetPasswordEmailStatus()
    }
}
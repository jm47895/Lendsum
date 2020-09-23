package com.lendsumapp.lendsum.viewmodel

import android.app.Activity
import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.lendsumapp.lendsum.repository.LoginRepository
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NumberVerificationViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository
): ViewModel(){

    fun sendSMSCode(phoneNumber: String, activity: Activity){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.sendSMSCode(phoneNumber, activity)
        }
    }

    fun getGeneratedPhoneAuthCode(): MutableLiveData<PhoneAuthCredential> {
        return loginRepository.getGeneratedPhoneAuthCode()
    }

    fun linkPhoneNumWithLoginCredential(credential: PhoneAuthCredential){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.linkPhoneNumWithLoginCredential(credential)
        }
    }
}
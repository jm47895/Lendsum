package com.lendsumapp.lendsum.viewmodel

import android.app.Activity
import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthCredential
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

    fun sendSMSCode(phoneNumber: String, activity: Activity){
        loginRepository.sendSMSCode(phoneNumber, activity)
    }

    fun getGeneratedPhoneAuthCode(): MutableLiveData<PhoneAuthCredential> {
        return loginRepository.getGeneratedPhoneAuthCode()
    }

    fun linkPhoneNumWithLoginCredential(credential: PhoneAuthCredential){
        loginRepository.linkPhoneNumWithLoginCredential(credential)
    }
}
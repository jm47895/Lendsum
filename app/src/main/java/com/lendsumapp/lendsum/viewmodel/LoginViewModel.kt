package com.lendsumapp.lendsum.viewmodel

import android.content.Intent
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.lendsumapp.lendsum.repository.LoginRepository

class LoginViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository
): ViewModel(){

    fun getFirebaseUser(): FirebaseUser?{
        return loginRepository.getFirebaseUser()
    }

    fun configureGoogleAuth(){
        loginRepository.configureGoogleAuth()
    }

    fun getGoogleAuthIntent(): Intent {
        return loginRepository.sendGoogleSignInIntent()
    }

    fun handleGoogleSignInIntent(resultCode: Int, data: Intent){
        loginRepository.handleGoogleSignInIntent(resultCode, data)
    }

    fun getGoogleAuthCode(): Int{
        return loginRepository.getGoogleRequestCode()
    }

    companion object{
        private val TAG = LoginViewModel::class.simpleName
    }

}
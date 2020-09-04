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

    //Start of Google Auth functions
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
    //End of Google Auth functions

    //Start of Email and Pass functions
    fun signInWithEmailAndPass(email: String, password: String){
        loginRepository.signInWithEmailAndPass(email, password)
    }

    fun initializeAuthStateListener(){
        loginRepository.initializeAuthStateListener()
    }

    fun dismissAuthStateListener(){
        loginRepository.dismissAuthStateListener()
    }

    fun addFirebaseAuthStateListener(){
        loginRepository.addFirebaseAuthStateListener()
    }

    fun getEmailSignInStatus(): MutableLiveData<Boolean>{
        return loginRepository.getEmailSignInStatus()
    }
    //End of Email and Pass functions

    //Facebook login functions
    fun sendFacebookIntent(){
        loginRepository.sendFacebookIntent()
    }

    fun handleFacebookSignInIntent(requestCode: Int, resultCode: Int, data: Intent){
        loginRepository.handleFacebookSignInIntent(requestCode, resultCode, data)
    }
    //End of Facebook login functions

    companion object{
        private val TAG = LoginViewModel::class.simpleName
    }

}
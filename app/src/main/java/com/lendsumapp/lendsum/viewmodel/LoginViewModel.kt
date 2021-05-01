package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.lendsumapp.lendsum.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
): ViewModel(){

    fun getFirebaseUser(): FirebaseUser?{
        return loginRepository.getFirebaseUser()
    }

    //Start of Google Auth functions
    fun configureGoogleAuth(context: Context){
        loginRepository.configureGoogleAuth(context)
    }

    fun getGoogleAuthIntent(): Intent {
        return loginRepository.sendGoogleSignInIntent()
    }

    fun handleGoogleSignInIntent(data: Intent){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.handleGoogleSignInIntent(data)
        }
    }

    fun getGoogleLoginState():MutableLiveData<Boolean>{
        return loginRepository.getGoogleLoginState()
    }
    //End of Google Auth functions

    //Start of Email and Pass functions
    fun signInWithEmailAndPass(email: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.signInWithEmailAndPass(email, password)
        }
    }

    fun getEmailSignInStatus(): MutableLiveData<Boolean>{
        return loginRepository.getEmailSignInStatus()
    }
    //End of Email and Pass functions

    //Facebook login functions
    fun sendFacebookIntent(){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.sendFacebookIntent()
        }
    }

    fun handleFacebookSignInIntent(requestCode: Int, resultCode: Int, data: Intent){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.handleFacebookSignInIntent(requestCode, resultCode, data)
        }
    }

    fun getFacebookAuthState(): MutableLiveData<Boolean>{
        return loginRepository.getFacebookAuthState()
    }
    //End of Facebook login functions

    companion object{
        private val TAG = LoginViewModel::class.simpleName
    }

}
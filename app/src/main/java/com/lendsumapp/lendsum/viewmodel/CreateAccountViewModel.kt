package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.lendsumapp.lendsum.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
): ViewModel(){

    fun logOutOfGoogle(){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.logOutOfGoogle()
        }
    }

    fun configureGoogleAuth(context: Context){
        loginRepository.configureGoogleAuth(context)
    }

    fun logOutOfFacebook(){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.logOutOfFacebook()
        }
    }

    fun getFirebaseUser(): FirebaseUser?{
        return loginRepository.getFirebaseUser()
    }

    fun createUserAccount(email: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.registerWithEmailAndPassword(email, password)
        }
    }

    fun getEmailCreateAccountStatus(): MutableLiveData<Boolean> {
        return loginRepository.getEmailCreateAccountStatus()
    }

    fun updateFirebaseAuthProfile(key: String, value: String){
        loginRepository.launchUpdateFirebaseAuthProfileWorker(key, value)
    }

    fun getLinkWithCredentialStatus(): MutableLiveData<Boolean> {
        return loginRepository.getLinkWithCredentialStatus()
    }

    fun logOutOfEmailAndPass(){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.logOutOfEmailAndPass()
        }
    }

    fun deleteFirebaseUser(){
        loginRepository.deleteFirebaseUser()
    }
}
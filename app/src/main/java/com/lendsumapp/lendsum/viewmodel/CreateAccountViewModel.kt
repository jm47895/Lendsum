package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.repository.LoginRepository
import com.lendsumapp.lendsum.util.AndroidUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
): ViewModel(){

    private val _createAccountError = mutableStateOf<LendsumError?>(null)

    val createAccountError: LendsumError?
     get() = _createAccountError.value

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

    fun isValidAccountForm(firstName: String, lastName: String, email: String, password: String, matchPassword: String): Boolean{
        return when{
            firstName.isEmpty() -> {
                _createAccountError.value = LendsumError.EMPTY_FIRST_NAME
                return false
            }
            lastName.isEmpty() -> {
                _createAccountError.value = LendsumError.EMPTY_LAST_NAME
                return false
            }
            !AndroidUtils.isValidEmail(email) -> {
                _createAccountError.value = LendsumError.INVALID_EMAIL
                return false
            }
            password.isEmpty() || !AndroidUtils.isValidPassword(password) -> {
                _createAccountError.value = LendsumError.INVALID_PASS
                return false
            }
            password != matchPassword -> {
                _createAccountError.value = LendsumError.PASS_NO_MATCH
                return false
            }
            else -> {
                _createAccountError.value = LendsumError.NONE
                true
            }
        }
    }
}
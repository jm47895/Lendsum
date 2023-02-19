package com.lendsumapp.lendsum.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.repository.LoginRepository
import com.lendsumapp.lendsum.util.AndroidUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
): ViewModel(){

    private val _createAccountState = mutableStateOf<Response<Unit>>(Response())
    private val _firebaseUser = mutableStateOf<FirebaseUser?>(null)

    val createAccountState: Response<Unit>
        get() = _createAccountState.value
    val firebaseUser: FirebaseUser?
        get() = _firebaseUser.value

    init {
        getFirebaseUser()
    }

    fun logOutOfGoogle(){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.logOutOfGoogle()
        }
    }

    fun logOutOfFacebook(){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.logOutOfFacebook()
        }
    }

    private fun getFirebaseUser(){
        _firebaseUser.value = loginRepository.getFirebaseUser()
    }

    fun createUserAccount(email: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.registerWithEmailAndPassword(email, password).collect{
                _createAccountState.value = it
            }
        }
    }

    fun updateFirebaseAuthProfile(key: String, value: String){
        loginRepository.launchUpdateFirebaseAuthProfileWorker(key, value)
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
                _createAccountState.value = Response(status = Status.ERROR, error = LendsumError.EMPTY_FIRST_NAME)
                return false
            }
            lastName.isEmpty() -> {
                _createAccountState.value = Response(status = Status.ERROR, error = LendsumError.EMPTY_LAST_NAME)
                return false
            }
            !AndroidUtils.isValidEmail(email) -> {
                _createAccountState.value = Response(status = Status.ERROR, error = LendsumError.INVALID_EMAIL)
                return false
            }
            password.isEmpty() || !AndroidUtils.isValidPassword(password) -> {
                _createAccountState.value = Response(status = Status.ERROR, error = LendsumError.INVALID_PASS)
                return false
            }
            password != matchPassword -> {
                _createAccountState.value = Response(status = Status.ERROR, error = LendsumError.PASS_NO_MATCH)
                return false
            }
            else -> {
                true
            }
        }
    }

    fun splitName(fullName: String): Pair<String,String>?{

        if(fullName.isEmpty()) return null

        val nameSplit = fullName.split("\\s".toRegex())

        if (nameSplit.isEmpty()) return null

        return if(nameSplit.size < 2){
            Pair(nameSplit[0], nameSplit[0])
        }else{
            Pair(nameSplit[0], nameSplit[1])
        }
    }

    fun resetCreateAccountState(){
        _createAccountState.value = Response()
    }
}
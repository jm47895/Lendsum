package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.repository.LoginRepository
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
): ViewModel(){

    private val _loginState = mutableStateOf(Response<Unit>())
    private val _resetPassState = mutableStateOf(Response<Unit>())
    private val _firebaseUser = mutableStateOf<FirebaseUser?>(null)
    private val _googleSignInState = mutableStateOf(Response<Unit>())

    val loginState: Response<Unit>
        get() = _loginState.value
    val firebaseUser: FirebaseUser?
        get() = _firebaseUser.value
    val resetPassState: Response<Unit>
        get() = _resetPassState.value
    val googleSignInState: Response<Unit>
        get() = _googleSignInState.value

    init {
        getFirebaseUser()
    }

    private fun getFirebaseUser(){
        _firebaseUser.value = loginRepository.getFirebaseUser()
    }

    //Start of Google Auth functions
    fun getGoogleAuthIntent(): Intent {
        return loginRepository.sendGoogleSignInIntent()
    }

    fun handleGoogleSignInIntent(data: Intent){
        viewModelScope.launch{
            loginRepository.handleGoogleSignInIntent(data).collect{
                _googleSignInState.value = it
            }
        }
    }
    //End of Google Auth functions

    //Start of Email and Pass functions
    fun signInWithEmailAndPass(context: Context, email: String, password: String){

        if(!NetworkUtils.isNetworkAvailable(context)){
            _loginState.value =
                Response(status = Status.ERROR, error = LendsumError.NO_INTERNET)
            return
        }

        viewModelScope.launch {
            loginRepository.signInWithEmailAndPass(email, password).collect{
                _loginState.value = it
            }
        }
    }

    fun sendPasswordResetEmail(context: Context, email: String){

        if(!NetworkUtils.isNetworkAvailable(context)){
            _resetPassState.value = Response(status = Status.ERROR, error = LendsumError.NO_INTERNET)
            return
        }

        if(!AndroidUtils.isValidEmail(email)){
            _resetPassState.value = Response(status = Status.ERROR, error = LendsumError.INVALID_EMAIL)
            return
        }

        viewModelScope.launch {
            loginRepository.sendPasswordResetEmail(email).collect{
                _resetPassState.value = it
            }
        }
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

    fun resetLoginState(){
        _loginState.value = Response()
    }

    fun resetGoogleSigInState(){
        _googleSignInState.value = Response()
    }

    companion object{
        private val TAG = LoginViewModel::class.simpleName
    }

}
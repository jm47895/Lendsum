package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.LoginRepository
import com.lendsumapp.lendsum.repository.NumberVerificationRepository
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    @ApplicationContext val context: Context
): ViewModel(){

    private val _loginState = mutableStateOf(Response<Unit>())
    private val _resetPassState = mutableStateOf(Response<Unit>())
    private val _firebaseUser = mutableStateOf<FirebaseUser?>(null)
    private val _googleSignInState = mutableStateOf(Response<Unit>())
    private val _syncDataState = mutableStateOf(Response<User>())

    val loginState: State<Response<Unit>>
        get() = _loginState
    val syncState: State<Response<User>>
        get() = _syncDataState
    val firebaseUser: State<FirebaseUser?>
        get() = _firebaseUser
    val resetPassState: State<Response<Unit>>
        get() = _resetPassState
    val googleSignInState: State<Response<Unit>>
        get() = _googleSignInState

    init {
        getFirebaseUser()
    }

    private fun getFirebaseUser(){
        _firebaseUser.value = loginRepository.getFirebaseUser()
    }

    //Start of Google Auth functions
    fun handleGoogleSignInIntent(data: Intent){
        viewModelScope.launch{
            loginRepository.handleGoogleSignInIntent(data).collect{
                _googleSignInState.value = it
            }
        }
    }
    //End of Google Auth functions

    //Start of Email and Pass functions
    fun signInWithEmailAndPass(email: String, password: String){

        _loginState.value = Response(status = Status.LOADING)

        when{
            !AndroidUtils.isValidEmail(email) || password.isEmpty() || email.isEmpty()-> {
                _loginState.value = Response(status = Status.ERROR, error = LendsumError.INVALID_LOGIN)
                return
            }

            !NetworkUtils.isNetworkAvailable(context) -> {
                _loginState.value = Response(status = Status.ERROR, error = LendsumError.NO_INTERNET)
                return
            }
        }

        viewModelScope.launch {
            _loginState.value = loginRepository.signInWithEmailAndPass(email, password)
        }
    }

    fun sendPasswordResetEmail(email: String){

        _resetPassState.value = Response(status = Status.LOADING)

        if(!NetworkUtils.isNetworkAvailable(context)){
            _resetPassState.value = Response(status = Status.ERROR, error = LendsumError.NO_INTERNET)
            return
        }

        if(!AndroidUtils.isValidEmail(email)){
            _resetPassState.value = Response(status = Status.ERROR, error = LendsumError.INVALID_EMAIL)
            return
        }

        viewModelScope.launch {
            _resetPassState.value = loginRepository.sendPasswordResetEmail(email)
        }
    }
    //End of Email and Pass functions

    //Sync functions
    fun syncUserData(){

        getFirebaseUser()

        firebaseUser.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                _syncDataState.value  = loginRepository.syncAllUserData()
            }
        }
    }
    //End of sync functions

    fun resetLoginState(){
        _loginState.value = Response()
    }

    fun resetGoogleSigInState(){
        _googleSignInState.value = Response()
    }

    fun resetSyncState(){
        _syncDataState.value = Response()
    }

    companion object{
        private val TAG = LoginViewModel::class.simpleName
    }

}
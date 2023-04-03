package com.lendsumapp.lendsum.viewmodel

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.LoginRepository
import com.lendsumapp.lendsum.repository.NumberVerificationRepository
import com.lendsumapp.lendsum.util.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class NumberVerificationViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val numberVerificationRepository: NumberVerificationRepository,
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext val context: Context
): ViewModel(){

    private val _phoneCodeState = mutableStateOf( Response<String>())
    private val _phoneLinkState = mutableStateOf( Response<Unit>())

    val phoneCodeState: State<Response<String>>
        get() = _phoneCodeState
    val phoneLinkState: State<Response<Unit>>
        get() = _phoneLinkState

    fun sendSMSCode(phoneNumber: String, activity: Activity){

        _phoneCodeState.value = Response(status = Status.LOADING)

        if(!NetworkUtils.isNetworkAvailable(activity)){
            _phoneCodeState.value = Response(status = Status.ERROR, error = LendsumError.NO_INTERNET)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _phoneCodeState.value = loginRepository.requestSMSCode(phoneNumber, activity)
        }
    }

    fun verifyPhoneNumber(inputCode: String){

        _phoneLinkState.value = Response(status = Status.LOADING)

        if(!NetworkUtils.isNetworkAvailable(context)){
            _phoneLinkState.value = Response(status = Status.ERROR, error = LendsumError.NO_INTERNET)
            return
        }

        _phoneCodeState.value.data?.let { verificationId ->

            val credential = PhoneAuthProvider.getCredential(verificationId, inputCode)

            linkPhoneNumWithLoginCredential(credential)
        }
    }

    private fun linkPhoneNumWithLoginCredential(credential: PhoneAuthCredential){
        viewModelScope.launch{
            _phoneLinkState.value = loginRepository.linkPhoneNumWithLoginCredential(credential)
        }
    }

     fun insertNewUserIntoSqlCache(){
        viewModelScope.launch(Dispatchers.IO) {

            val user = getNewUserObject()

            numberVerificationRepository.insertUserIntoSqlCache(user)
        }
    }

    fun insertNewUserIntoFirestoreDb(){

        val user = getNewUserObject()

        numberVerificationRepository.insertUserIntoFirestore(user)
    }

    private fun getNewUserObject(): User{
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser

        val uid = firebaseUser?.uid.toString()
        val displayName = firebaseUser?.displayName.toString()
        val userName =  createUsername(firebaseUser?.displayName.toString(), firebaseUser?.uid.toString())
        val email = firebaseUser?.email.toString()
        val phoneNumber = firebaseUser?.phoneNumber.toString()
        val profPhoto = firebaseUser?.photoUrl.toString()

        return User(uid, displayName, userName, email, phoneNumber, profPhoto, karmaScore = 100, friendList = null, isProfilePublic = true)
    }

    private fun createUsername(name: String, uid: String): String{
        val firstName = name.substring(0, name.indexOf(" "))
        val firstFiveUidDigits = uid.substring(0, 5)

        return "@$firstName-$firstFiveUidDigits"
    }

    fun resetLinkState(){
        _phoneLinkState.value = Response()
    }

    fun resetPhoneCodeState(){
        _phoneCodeState.value = Response()
    }

    companion object{
        private val TAG = NumberVerificationViewModel::class.simpleName
    }
}
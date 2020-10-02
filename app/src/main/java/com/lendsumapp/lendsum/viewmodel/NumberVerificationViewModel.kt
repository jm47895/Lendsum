package com.lendsumapp.lendsum.viewmodel

import android.app.Activity
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.LoginRepository
import com.lendsumapp.lendsum.repository.NumberVerificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NumberVerificationViewModel @ViewModelInject constructor(
    private val loginRepository: LoginRepository,
    private val numberVerificationRepository: NumberVerificationRepository,
    private val firebaseAuth: FirebaseAuth
): ViewModel(){

    fun sendSMSCode(phoneNumber: String, activity: Activity){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.sendSMSCode(phoneNumber, activity)
        }
    }

    fun getGeneratedPhoneAuthCode(): MutableLiveData<PhoneAuthCredential> {
        return loginRepository.getGeneratedPhoneAuthCode()
    }

    fun linkPhoneNumWithLoginCredential(credential: PhoneAuthCredential){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.linkPhoneNumWithLoginCredential(credential)
        }
    }

    fun storeUserCredentialsInCacheAndFirestore(){
        viewModelScope.launch(Dispatchers.IO) {
            val user = getCurrentUserObject()

            insertUserIntoSqlCache(user)
            insertUserIntoFirestoreDb(user)
        }
    }

    private suspend fun insertUserIntoSqlCache(user: User){

        numberVerificationRepository.insertUserIntoSqlCache(user)
    }

    private fun insertUserIntoFirestoreDb(user: User){

        numberVerificationRepository.insertUserIntoFirestore(user)
    }


    private fun getCurrentUserObject(): User{
        val firebaseUser = firebaseAuth.currentUser!!

        return User(firebaseUser.uid,
            firebaseUser.displayName.toString(),
            "@" + firebaseUser.displayName,
            firebaseUser.email.toString(), firebaseUser.phoneNumber.toString(),firebaseUser.photoUrl.toString(), karmaScore = 100, friendList = null)
    }

    fun getPhoneNumberLinkStatus(): MutableLiveData<Boolean>{
        return loginRepository.getPhoneNumberLinkStatus()
    }

    companion object{
        private val TAG = NumberVerificationViewModel::class.simpleName
    }
}
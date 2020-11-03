package com.lendsumapp.lendsum.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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

    private val cacheDbStatus: MutableLiveData<Boolean> = MutableLiveData()


    fun getCacheStatus(): MutableLiveData<Boolean> {
        return cacheDbStatus
    }

    fun sendSMSCode(phoneNumber: String, activity: Activity){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.sendSMSCode(phoneNumber, activity)
        }
    }

    fun getGeneratedPhoneAuthCode(): MutableLiveData<PhoneAuthCredential> {
        return loginRepository.getGeneratedPhoneAuthCode()
    }

    fun getPhoneNumberLinkStatus(): MutableLiveData<Boolean>{
        return loginRepository.getPhoneNumberLinkStatus()
    }

    fun linkPhoneNumWithLoginCredential(credential: PhoneAuthCredential){
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.linkPhoneNumWithLoginCredential(credential)
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
        val firebaseUser = firebaseAuth.currentUser!!

        return User(firebaseUser.uid,
            firebaseUser.displayName.toString(),
            createUsername(firebaseUser.displayName.toString(), firebaseUser.uid),
            firebaseUser.email.toString(), firebaseUser.phoneNumber.toString(),firebaseUser.photoUrl.toString(), karmaScore = 100, friendList = null, isProfilePublic = true)
    }

    private fun createUsername(name: String, uid: String): String{
        val firstName = name.substring(0, name.indexOf(" "))
        val firstFiveUidDigits = uid.substring(0, 5)

        return "@$firstName-$firstFiveUidDigits"
    }

    //Sync data functions
    fun doesLendsumDbCacheExist(context: Context, dbName: String){
        viewModelScope.launch(Dispatchers.IO) {
            cacheDbStatus.postValue(numberVerificationRepository.doesLendsumDbCacheExist(context, dbName))
        }
    }

    fun syncAllDataFromDatabases(uid: String){
        viewModelScope.launch(Dispatchers.IO) {
            numberVerificationRepository.syncAllDataFromDatabases(uid)
        }
    }
    //End sync data functions

    companion object{
        private val TAG = NumberVerificationViewModel::class.simpleName
    }
}
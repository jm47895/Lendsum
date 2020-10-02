package com.lendsumapp.lendsum.viewmodel

import android.app.Activity
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

    private val doesCacheExist: MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var firestoreUserObserver: Observer<User>

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

    fun checkIfSignInCacheDbExists(dbName: String){
        viewModelScope.launch(Dispatchers.IO) {
            doesCacheExist.postValue(numberVerificationRepository.doesDbCacheExist(dbName))
        }
    }

    fun getCacheStatus(): MutableLiveData<Boolean>{
        return doesCacheExist
    }

    fun insertNewUserIntoFirestoreDb(){

        val user = getNewUserObject()

        numberVerificationRepository.insertUserIntoFirestore(user)
    }

    fun getExistingUserFromFirestore(){
        firestoreUserObserver = Observer {
            Log.d(TAG, "Existing user observer hit: $it")
            insertExistingUserIntoSqlCache(it)
            numberVerificationRepository.getRemoteUser().removeObserver(firestoreUserObserver)
        }
        numberVerificationRepository.getRemoteUser().observeForever(firestoreUserObserver)
        numberVerificationRepository.getExistingUserFromFirestore(firebaseAuth.currentUser?.uid.toString())
    }

    private fun insertExistingUserIntoSqlCache(user: User) {
        viewModelScope.launch(Dispatchers.IO){
            Log.d(TAG, "Caching $user")
            numberVerificationRepository.insertUserIntoSqlCache(user)
        }
    }


    private fun getNewUserObject(): User{
        val firebaseUser = firebaseAuth.currentUser!!

        return User(firebaseUser.uid,
            firebaseUser.displayName.toString(),
            "@" + firebaseUser.displayName,
            firebaseUser.email.toString(), firebaseUser.phoneNumber.toString(),firebaseUser.photoUrl.toString(), karmaScore = 100, friendList = null)
    }


    companion object{
        private val TAG = NumberVerificationViewModel::class.simpleName
    }
}
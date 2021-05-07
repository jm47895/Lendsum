package com.lendsumapp.lendsum.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.LoginRepository
import com.lendsumapp.lendsum.repository.NumberVerificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NumberVerificationViewModel @Inject constructor(
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

    //Sync data functions
    fun checkIfUserExistsInLendsumDbCache(): LiveData<User>{
        val firebaseUser = firebaseAuth.currentUser!!

        return numberVerificationRepository.doesUserExistInLendsumDbCache(firebaseUser).asLiveData()

    }

    fun syncUserData(uid: String){
        viewModelScope.launch(Dispatchers.IO) {
            numberVerificationRepository.syncUserData(uid, viewModelScope)
        }
    }
    //End sync data functions

    companion object{
        private val TAG = NumberVerificationViewModel::class.simpleName
    }
}
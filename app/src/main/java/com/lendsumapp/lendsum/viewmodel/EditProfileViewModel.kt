package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.EditProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileViewModel @ViewModelInject constructor(
    private val editProfileRepository: EditProfileRepository,
    private var firebaseAuth: FirebaseAuth?
) :ViewModel(){

    private val user: MutableLiveData<User> = MutableLiveData()
    private val updateUserStatus: MutableLiveData<Int> = MutableLiveData()

    //Room cache sql functions
    fun getCachedUser(){
        viewModelScope.launch(Dispatchers.IO) {
            val cachedUser = editProfileRepository.getCachedUser(firebaseAuth?.currentUser?.uid.toString())
            user.postValue(cachedUser)
        }
    }

    fun getUser(): MutableLiveData<User> {
        return user
    }

    fun updateCachedUser(userObject: User){
        viewModelScope.launch(Dispatchers.IO) {
            val userStatus = editProfileRepository.updateCachedUser(userObject)

            updateUserStatus.postValue(userStatus)
        }
    }

    fun getUpdateCacheUserStatus(): MutableLiveData<Int>{
        return updateUserStatus
    }
    //End of room cache sql functions

    //Firebase auth functions
    fun updateAuthEmail(email: String){
        viewModelScope.launch(Dispatchers.IO) {
            editProfileRepository.updateAuthEmail(email)
        }
    }

    fun getUpdateAuthEmailStatus(): MutableLiveData<Boolean> {
        return editProfileRepository.getUpdateAuthEmailStatus()
    }

    fun updateFirebaseAuthProfile(key: String, value: String){
        viewModelScope.launch(Dispatchers.IO) {
            editProfileRepository.updateFirebaseAuthProfile(key, value)
        }
    }
    //End of firebase auth functions

    //Firestore functions
    fun updateUserValueInFirestore(key: String, value: String){
        viewModelScope.launch(Dispatchers.IO) {
            editProfileRepository.updateUserValueInFirestore(key, value)
        }
    }
    //End of firestore functions


    companion object {
        private val TAG = EditProfileViewModel::class.simpleName
    }
}
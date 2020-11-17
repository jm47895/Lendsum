package com.lendsumapp.lendsum.viewmodel

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.EditProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileViewModel @ViewModelInject constructor(
    private val editProfileRepository: EditProfileRepository,
    private var firebaseAuth: FirebaseAuth?
) :ViewModel(){

    private val updateUserStatus: MutableLiveData<Int> = MutableLiveData()

    //Room cache sql functions
    fun getCachedUser(): LiveData<User>{

        val uid = firebaseAuth?.currentUser?.uid.toString()

        return editProfileRepository.getCachedUser(uid).asLiveData()

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

    fun updateAuthPass(password: String){
        viewModelScope.launch {
            editProfileRepository.updateAuthPass(password)
        }
    }

    fun getUpdateAuthPassStatus():MutableLiveData<Boolean>{
        return editProfileRepository.getUpdateAuthPassStatus()
    }
    //End of firebase auth functions

    //Firestore functions
    fun updateUserValueInFirestore(key: String, stringValue: String?, booleanValue: Boolean?){
        viewModelScope.launch(Dispatchers.IO) {
            editProfileRepository.updateUserValueInFirestore(key, stringValue, booleanValue)
        }
    }
    //End of firestore functions

    //Firebase storage functions
    fun uploadProfilePhotoToFirebaseStorage(fileName: String, uri: Uri){
        viewModelScope.launch(Dispatchers.IO){
            editProfileRepository.uploadProfilePhotoToFirebaseStorage(fileName, uri)
        }
    }
    //End firebase storage functions


    companion object {
        private val TAG = EditProfileViewModel::class.simpleName
    }
}
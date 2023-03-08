package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.AccountRepository
import com.lendsumapp.lendsum.util.DatabaseUtils
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_EMAIL_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_PROFILE_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_USERNAME_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private var firebaseAuth: FirebaseAuth?,
    @ApplicationContext private val context: Context
) :ViewModel(){

    private val _currentUser = mutableStateOf<User?>(null)

    val currentUser: User?
        get() = _currentUser.value

    private val updateUserStatus: MutableLiveData<Int> = MutableLiveData()

    init {
        getCachedUser()
    }

    //Room cache sql functions
    private fun getCachedUser(){

        val uid = firebaseAuth?.currentUser?.uid.toString()

        viewModelScope.launch {
            accountRepository.getCachedUser(uid).collect{ user ->
                _currentUser.value = user
            }
        }
    }

    private fun updateLocalCachedUser(userObject: User){
        viewModelScope.launch(Dispatchers.IO) {
            val userStatus = accountRepository.updateLocalCachedUser(userObject)

            updateUserStatus.postValue(userStatus)
        }
    }

    fun updateProfile(user: User){
        //update cache
        updateLocalCachedUser(user)
        //update firebase auth
        updateFirebaseAuthProfile(FIREBASE_PROFILE_NAME_KEY, user.name)
        updateFirebaseAuthProfile(FIREBASE_USERNAME_KEY, user.username)
        updateFirebaseAuthProfile(FIREBASE_EMAIL_KEY, user.email)
        //update firebase firestore
        updateUserValueInFirestore(FIREBASE_PROFILE_NAME_KEY, user.name)
        updateUserValueInFirestore(FIREBASE_USERNAME_KEY, user.username)
        updateUserValueInFirestore(FIREBASE_EMAIL_KEY, user.email)


    }

    fun getUpdateCacheUserStatus(): MutableLiveData<Int>{
        return updateUserStatus
    }
    //End of room cache sql functions

    //Firebase auth functions
    fun updateAuthEmail(email: String){
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.updateAuthEmail(email)
        }
    }

    fun getUpdateAuthEmailStatus(): MutableLiveData<Boolean> {
        return accountRepository.getUpdateAuthEmailStatus()
    }

    private fun updateFirebaseAuthProfile(key: String, value: String){
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.updateFirebaseAuthProfile(key, value)
        }
    }

    fun updateAuthPass(password: String){
        viewModelScope.launch {
            accountRepository.updateAuthPass(password)
        }
    }

    fun getUpdateAuthPassStatus():MutableLiveData<Boolean>{
        return accountRepository.getUpdateAuthPassStatus()
    }
    //End of firebase auth functions

    //Firestore functions
    fun updateUserValueInFirestore(key: String, value: Any){
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.launchUpdateFirestoreUserValueWorker(key, value)
        }
    }
    //End of firestore functions

    //Firebase storage functions
    fun uploadProfilePhoto(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO){
            val user = firebaseAuth?.currentUser
            val fileName = user?.uid + "." + DatabaseUtils.getFileExtension(context, uri)

            accountRepository.launchUploadImageWorkers(fileName, uri)
        }
    }
    //End firebase storage functions


    companion object {
        private val TAG = AccountViewModel::class.simpleName
    }
}
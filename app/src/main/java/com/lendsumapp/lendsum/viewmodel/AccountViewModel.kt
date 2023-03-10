package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
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
    private val workManager: WorkManager,
    @ApplicationContext private val context: Context
) :ViewModel(){

    private val _currentUser = mutableStateOf<User?>(null)
    private val _updateProfileState = mutableStateOf(Response<Unit>())

    val currentUser: User?
        get() = _currentUser.value
    val updateProfileState: Response<Unit>
        get() = _updateProfileState.value

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

    fun updateProfile(lifecycleOwner: LifecycleOwner, user: User){
        //update cache
        updateLocalCachedUser(user)
        //update firebase auth profile
        updateFirebaseAuthProfile(lifecycleOwner, user)
        //update firebase firestore
        updateUserFirestore(lifecycleOwner, user)


    }

    fun getUpdateCacheUserStatus(): MutableLiveData<Int>{
        return updateUserStatus
    }

    //Firebase auth functions
    fun updateAuthEmail(email: String){
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.updateAuthEmail(email)
        }
    }

    fun getUpdateAuthEmailStatus(): MutableLiveData<Boolean> {
        return accountRepository.getUpdateAuthEmailStatus()
    }

    private fun updateFirebaseAuthProfile(lifecycleOwner: LifecycleOwner, user : User){
        viewModelScope.launch {

            val workerId = accountRepository.updateFirebaseAuthProfile(user)

            workManager.getWorkInfoByIdLiveData(workerId).observe(lifecycleOwner, Observer { workInfo ->
                when(workInfo.state){
                    WorkInfo.State.ENQUEUED -> { _updateProfileState.value = Response(status = Status.LOADING) }
                    WorkInfo.State.RUNNING -> { _updateProfileState.value = Response(status = Status.LOADING) }
                    WorkInfo.State.SUCCEEDED -> {
                        _updateProfileState.value = Response(status = Status.SUCCESS)
                    }
                    WorkInfo.State.FAILED -> { _updateProfileState.value = Response(status = Status.ERROR) }
                    WorkInfo.State.BLOCKED -> { _updateProfileState.value = Response(status = Status.ERROR) }
                    WorkInfo.State.CANCELLED -> { Log.i(TAG, "Work was cancelled.")}
                }
            })
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

    //Firestore functions
    fun updateUserFirestore(lifecycleOwner: LifecycleOwner, user: User){

        val workerId = accountRepository.launchUpdateFirestoreUserValueWorker(user)

        workManager.getWorkInfoByIdLiveData(workerId).observe(lifecycleOwner, Observer { workInfo ->
            Log.d(TAG, "Firestore worker state: ${workInfo.state}")
            /*when(workInfo.state){
                WorkInfo.State.ENQUEUED -> { _updateProfileState.value = Response(status = Status.LOADING) }
                WorkInfo.State.RUNNING -> { _updateProfileState.value = Response(status = Status.LOADING) }
                WorkInfo.State.SUCCEEDED -> {
                    //_updateProfileState.value = Response(status = Status.SUCCESS)
                }
                WorkInfo.State.FAILED -> { _updateProfileState.value = Response(status = Status.ERROR) }
                WorkInfo.State.BLOCKED -> { _updateProfileState.value = Response(status = Status.ERROR) }
                WorkInfo.State.CANCELLED -> { Log.i(TAG, "Work was cancelled.")}
            }*/
        })
    }

    //Firebase storage functions
    fun uploadProfilePhoto(uri: Uri) {
        val user = firebaseAuth?.currentUser
        val fileName = user?.uid + "." + DatabaseUtils.getFileExtension(context, uri)

        accountRepository.launchUploadImageWorkers(fileName, uri)
    }

    fun resetUpdateProfileState(){
        _updateProfileState.value = Response()
    }

    companion object {
        private val TAG = AccountViewModel::class.simpleName
    }
}
package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.AccountRepository
import com.lendsumapp.lendsum.util.DatabaseUtils
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
            accountRepository.updateLocalCachedUser(userObject)
        }
    }

    fun updateProfile(lifecycleOwner: LifecycleOwner, user: User){

        val workerId = accountRepository.launchUpdateProfileWorker(user)

        workManager.getWorkInfoByIdLiveData(workerId).observe(lifecycleOwner, Observer { workInfo ->
            workInfo?.let {
                Log.d(TAG, "Profile worker state ${workInfo.state}")

                when(workInfo.state){
                    WorkInfo.State.ENQUEUED -> { }
                    WorkInfo.State.RUNNING -> {
                        _updateProfileState.value = Response(status = Status.LOADING)
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        _updateProfileState.value = Response(status = Status.SUCCESS)
                        updateLocalCachedUser(user)
                    }
                    WorkInfo.State.FAILED -> { _updateProfileState.value = Response(status = Status.ERROR) }
                    WorkInfo.State.BLOCKED -> { Log.i(TAG, "Work is blocked.") }
                    WorkInfo.State.CANCELLED -> { Log.i(TAG, "Work was cancelled.")}
                }
            }
        })

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

    fun updateAuthPass(password: String){
        viewModelScope.launch {
            accountRepository.updateAuthPass(password)
        }
    }

    fun getUpdateAuthPassStatus():MutableLiveData<Boolean>{
        return accountRepository.getUpdateAuthPassStatus()
    }

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
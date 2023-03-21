package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.AccountRepository
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.DatabaseUtils
import com.lendsumapp.lendsum.util.NetworkUtils
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
) :ViewModel(){

    private val _currentUser = mutableStateOf<User?>(null)
    private val _updateProfileState = mutableStateOf(Response<Unit>())

    val currentUser: State<User?>
        get() = _currentUser
    val updateProfileState: State<Response<Unit>>
        get() = _updateProfileState

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

    fun updateProfile(context: Context, lifecycleOwner: LifecycleOwner, name: String, username: String){

        _updateProfileState.value = Response(status = Status.LOADING)

        currentUser.value?.let { currentUser ->

            val user = User(
                userId = currentUser.userId,
                name = name,
                username = if(!username.startsWith("@")) "@$username" else username,
                email = currentUser.email,
                phoneNumber = currentUser.phoneNumber,
                profilePicUri = currentUser.profilePicUri,
                karmaScore = currentUser.karmaScore,
                friendList = currentUser.friendList,
                isProfilePublic = currentUser.isProfilePublic
            )

            when{
                user.name.isEmpty() -> {
                    _updateProfileState.value = Response(status = Status.ERROR, error = LendsumError.EMPTY_NAME)
                    return
                }
                //This also means isEmpty but accounts for the added @ symbol if user does not include it
                user.username.length < 2-> {
                    _updateProfileState.value = Response(status = Status.ERROR, error = LendsumError.EMPTY_USERNAME)
                    return
                }
                !NetworkUtils.isNetworkAvailable(context) -> {
                    _updateProfileState.value = Response(status = Status.ERROR, error = LendsumError.OFFLINE_MODE)
                    //We don't want to return because we use WorkManager to defer the work when the user comes back online.
                }
            }

            val workerId = accountRepository.launchUpdateProfileWorker(user)

            workManager.getWorkInfoByIdLiveData(workerId).observe(lifecycleOwner, Observer { workInfo ->
                workInfo?.let {
                    Log.d(TAG, "Profile worker state ${workInfo.state}")

                    when(workInfo.state){
                        WorkInfo.State.ENQUEUED -> {}
                        WorkInfo.State.RUNNING -> {}
                        WorkInfo.State.SUCCEEDED -> {
                            _updateProfileState.value = Response(status = Status.SUCCESS)
                            updateLocalCachedUser(user)
                        }
                        WorkInfo.State.FAILED -> { _updateProfileState.value = Response(status = Status.ERROR, error = LendsumError.FAILED_TO_UPDATE_PROFILE) }
                        WorkInfo.State.BLOCKED -> { Log.i(TAG, "Work is blocked.") }
                        WorkInfo.State.CANCELLED -> { Log.i(TAG, "Work was cancelled.")}
                    }
                }
            })
        } ?: return
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

    fun uploadProfilePhoto(context: Context, lifecycleOwner: LifecycleOwner, uri: Uri) {
        val user = firebaseAuth?.currentUser
        val fileName = user?.uid + "." + DatabaseUtils.getFileExtension(context, uri)

        val workerId = accountRepository.launchUploadImageWorkers(fileName, uri)

        workManager.getWorkInfoByIdLiveData(workerId).observe(lifecycleOwner, Observer { workInfo ->
            workInfo?.let {
                Log.d(TAG, "Uri worker state ${workInfo.state}")

                when(workInfo.state){
                    WorkInfo.State.ENQUEUED -> {}
                    WorkInfo.State.RUNNING -> {}
                    WorkInfo.State.SUCCEEDED -> { _updateProfileState.value = Response(status = Status.SUCCESS) }
                    WorkInfo.State.FAILED -> { _updateProfileState.value = Response(status = Status.ERROR, error = LendsumError.FAILED_TO_UPDATE_PROFILE) }
                    WorkInfo.State.BLOCKED -> { Log.i(TAG, "Work is blocked.") }
                    WorkInfo.State.CANCELLED -> { Log.i(TAG, "Work was cancelled.")}
                }
            }
        })
    }

    fun resetUpdateProfileState(){
        _updateProfileState.value = Response()
    }

    companion object {
        private val TAG = AccountViewModel::class.simpleName
    }
}
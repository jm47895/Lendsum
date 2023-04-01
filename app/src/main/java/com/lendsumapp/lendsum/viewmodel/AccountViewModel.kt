package com.lendsumapp.lendsum.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
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
    private val _updateEmailState = mutableStateOf(Response<Unit>())
    private val _updatePassState = mutableStateOf(Response<Unit>())

    val currentUser: State<User?>
        get() = _currentUser
    val updateProfileState: State<Response<Unit>>
        get() = _updateProfileState
    val updateEmailState: State<Response<Unit>>
        get() = _updateEmailState
    val updatePassState: State<Response<Unit>>
        get() = _updatePassState

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

    fun updateProfile(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        name: String? = null,
        username: String? = null,
        profilePicUri: String? = null,
        email: String? = null
    ){

        _updateProfileState.value = Response(status = Status.LOADING)

        currentUser.value?.let { currentUser ->

            val user = User(
                userId = currentUser.userId,
                name = name ?: currentUser.name,
                username = username?.let {
                    //Add on @ symbol if not present
                    if(!username.startsWith("@")) "@$username" else username
                } ?: currentUser.username,
                email = email ?: currentUser.email,
                phoneNumber = currentUser.phoneNumber,
                profilePicUri = profilePicUri ?: currentUser.profilePicUri,
                karmaScore = currentUser.karmaScore,
                friendList = currentUser.friendList,
                isProfilePublic = currentUser.isProfilePublic
            )

            when{
                user.name.isEmpty() -> {
                    _updateProfileState.value = Response(status = Status.ERROR, error = LendsumError.EMPTY_NAME)
                    return
                }
                //This accounts for the added @ symbol if user does not include characters after.
                user.username.length < 2-> {
                    _updateProfileState.value = Response(status = Status.ERROR, error = LendsumError.EMPTY_USERNAME)
                    return
                }
                user.email.isEmpty() ->{
                    _updateProfileState.value = Response(status = Status.ERROR, error = LendsumError.INVALID_EMAIL)
                    return
                }
                !AndroidUtils.isValidEmail(user.email) ->{
                    _updateProfileState.value = Response(status = Status.ERROR, error = LendsumError.INVALID_EMAIL)
                    return
                }
                !NetworkUtils.isNetworkAvailable(context) -> {
                    _updateProfileState.value = Response(status = Status.ERROR, error = LendsumError.OFFLINE_MODE)
                    //We don't want to return because we use WorkManager to defer the work when the user comes back online.
                }
            }

            launchUpdateProfileWorker(lifecycleOwner, user)

        } ?: return
    }

    private fun launchUpdateProfileWorker(lifecycleOwner: LifecycleOwner, user: User){
        val workerId = accountRepository.launchUpdateProfileWorker(user)

        workManager.getWorkInfoByIdLiveData(workerId).observe(lifecycleOwner, Observer { workInfo ->
            workInfo?.let {
                when(workInfo.state){
                    WorkInfo.State.SUCCEEDED -> {
                        _updateProfileState.value = Response(status = Status.SUCCESS)
                    }
                    WorkInfo.State.FAILED -> { _updateProfileState.value = Response(status = Status.ERROR, error = LendsumError.FAILED_TO_UPDATE_PROFILE) }
                    WorkInfo.State.BLOCKED -> { Log.i(TAG, "Profile Work is blocked.") }
                    WorkInfo.State.CANCELLED -> { Log.i(TAG, "Profile Work was cancelled.")}
                    else -> { /*Don't care*/ }
                }
            }
        })
    }

    //Firebase auth functions
    fun updateAuthEmail(context: Context, email: String){

        _updateEmailState.value = Response(status = Status.LOADING)

        when{
            email.isEmpty() ||  !AndroidUtils.isValidEmail(email)-> {
                _updateEmailState.value = Response(status = Status.ERROR, error = LendsumError.INVALID_EMAIL)
                return
            }
            !NetworkUtils.isNetworkAvailable(context) -> {
                _updateEmailState.value = Response(status = Status.ERROR, error = LendsumError.NO_INTERNET)
                return
            }
        }

        viewModelScope.launch{
            _updateEmailState.value = accountRepository.updateAuthEmail(email)
        }
    }

    fun updateAuthPass(password: String, matchPass: String){
        viewModelScope.launch {
            accountRepository.updateAuthPass(password)
        }
    }


    fun uploadProfilePhoto(context: Context, lifecycleOwner: LifecycleOwner, uri: Uri) {

        val user = firebaseAuth?.currentUser
        val fileName = user?.uid + "." + DatabaseUtils.getFileExtension(context, uri)

        val workerId = accountRepository.launchUploadImageWorkers(fileName, uri)

        workManager.getWorkInfoByIdLiveData(workerId).observe(lifecycleOwner, Observer { workInfo ->
            workInfo?.let {

                when(workInfo.state){
                    WorkInfo.State.SUCCEEDED -> { _updateProfileState.value = Response(status = Status.SUCCESS) }
                    WorkInfo.State.FAILED -> { _updateProfileState.value = Response(status = Status.ERROR, error = LendsumError.FAILED_TO_UPDATE_PROFILE) }
                    WorkInfo.State.BLOCKED -> { Log.i(TAG, "Uri Work is blocked.") }
                    WorkInfo.State.CANCELLED -> { Log.i(TAG, "Uri Work was cancelled.")}
                    else -> { /*Don't care*/ }
                }
            }
        })
    }

    fun resetUpdateProfileState(){
        _updateProfileState.value = Response()
    }

    fun resetUpdateEmailState(){
        _updateEmailState.value = Response()
    }

    companion object {
        private val TAG = AccountViewModel::class.simpleName
    }
}
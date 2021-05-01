package com.lendsumapp.lendsum.viewmodel

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.NumberVerificationRepository
import com.lendsumapp.lendsum.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private var firebaseAuth: FirebaseAuth?
): ViewModel(){

    private val user: MutableLiveData<User> = MutableLiveData()

    fun getCachedUser(): LiveData<User>{

        val uid = firebaseAuth?.currentUser?.uid.toString()

        return profileRepository.getCachedUser(uid).asLiveData()

    }

    fun getUser(): MutableLiveData<User>{
        return user
    }

}
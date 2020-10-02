package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.NumberVerificationRepository
import com.lendsumapp.lendsum.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository,
    private var firebaseAuth: FirebaseAuth?
): ViewModel(){

    private val user: MutableLiveData<User> = MutableLiveData()

    fun getCachedUser(){
         viewModelScope.launch(Dispatchers.IO) {
             val cachedUser = profileRepository.getCachedUser(firebaseAuth?.currentUser?.uid.toString())
             user.postValue(cachedUser)
         }
    }

    fun getUser(): MutableLiveData<User>{
        return user
    }

}
package com.lendsumapp.lendsum.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository,
    private var firebaseAuth: FirebaseAuth?
): ViewModel(){

    private val user: MutableLiveData<User> = MutableLiveData()

    fun getCacheDisplayName(){
         viewModelScope.launch(Dispatchers.IO) {
             val cacheUser = profileRepository.getCacheUser(firebaseAuth?.currentUser?.uid.toString())
             user.postValue(cacheUser)
         }
    }

    fun getUser(): MutableLiveData<User>{
        return user
    }

}
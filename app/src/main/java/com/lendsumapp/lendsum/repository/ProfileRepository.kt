package com.lendsumapp.lendsum.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ProfileRepository @Inject constructor(
    private val database: LendsumDatabase,
    private var firebaseAuth: FirebaseAuth?
){

    fun getFirebaseDisplayName(): String{
        return firebaseAuth?.currentUser?.displayName.toString()
    }

    suspend fun getCacheUser(userId: String): User {
        return database.getUserDao().getUser(userId)
    }


}
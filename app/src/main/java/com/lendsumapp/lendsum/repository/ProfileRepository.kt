package com.lendsumapp.lendsum.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val database: LendsumDatabase,
    private var firebaseAuth: FirebaseAuth?
){

    private val remoteUser: MutableLiveData<User> = MutableLiveData()

    fun getFirebaseDisplayName(): String{
        return firebaseAuth?.currentUser?.displayName.toString()
    }

    suspend fun getCachedUser(userId: String): User {
        return database.getUserDao().getUser(userId)
    }


    companion object{
        private val TAG = ProfileRepository::class.simpleName
    }

}
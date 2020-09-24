package com.lendsumapp.lendsum.repository

import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@ActivityScoped
class NumberVerificationRepository @Inject constructor(
    private val database: LendsumDatabase
) {

    //Cache functions
    suspend fun insertUserIntoSqlCache(user: User){
        database.getUserDao().insertUser(user)
    }
    //End cache functions
}
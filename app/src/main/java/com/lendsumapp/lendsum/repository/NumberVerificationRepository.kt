package com.lendsumapp.lendsum.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants.USER_COLLECTION_PATH
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class NumberVerificationRepository @Inject constructor(
    private val cacheDb: LendsumDatabase,
    private val firestoreDb: FirebaseFirestore
) {

    //Cache functions
    suspend fun insertUserIntoSqlCache(user: User){
        cacheDb.getUserDao().insertUser(user)
    }
    //End cache functions

    //Firestore functions
    fun insertUserIntoFirestore(user: User){

        firestoreDb.collection(USER_COLLECTION_PATH).document(user.userId).set(user, SetOptions.merge())
            .addOnSuccessListener {
                Log.w(TAG, "User added to firestore")
            }
            .addOnFailureListener {
                Log.w(TAG, "Error adding user to firestore: $it")
            }

    }
    //End firestore functions

    companion object{
        private val TAG = NumberVerificationRepository::class.simpleName
    }
}
package com.lendsumapp.lendsum.repository

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.lendsumapp.lendsum.data.DataSyncManager
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_USER_COLLECTION_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NumberVerificationRepository @Inject constructor(
    private val cacheDb: LendsumDatabase,
    private val firestoreDb: FirebaseFirestore,
) {

    //Cache functions
    suspend fun insertUserIntoSqlCache(user: User){
        cacheDb.getUserDao().insertUser(user)
    }

    //End cache functions

    //Firestore functions
    fun insertUserIntoFirestore(user: User){

        firestoreDb.collection(FIREBASE_USER_COLLECTION_PATH).document(user.userId).set(user, SetOptions.merge())
            .addOnSuccessListener {
                Log.w(TAG, "User added to firestore")
            }
            .addOnFailureListener {
                Log.w(TAG, "Error adding user to firestore: $it")
            }

    }
    //End firestore functions

    //Sync user data function
    fun doesUserExistInLendsumDbCache(firebaseUser: FirebaseUser): Flow<User?>{
        return cacheDb.getUserDao().getUser(firebaseUser.uid)
    }
    //End Sync data functions

    companion object{
        private val TAG = NumberVerificationRepository::class.simpleName
    }
}
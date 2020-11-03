package com.lendsumapp.lendsum.repository

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.DataSyncManager
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.DatabaseUtils
import com.lendsumapp.lendsum.util.GlobalConstants.USER_COLLECTION_PATH
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class NumberVerificationRepository @Inject constructor(
    private val cacheDb: LendsumDatabase,
    private val firestoreDb: FirebaseFirestore,
    private val dataSyncManager: DataSyncManager
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

    //Sync data functions
    fun doesLendsumDbCacheExist(context: Context, dbName: String): Boolean{
        return dataSyncManager.doesLendsumDbExist(context, dbName)
    }

    fun syncAllDataFromDatabases(uid: String){
        dataSyncManager.syncAllDataFromDatabases(uid)
    }
    //End Sync data functions

    companion object{
        private val TAG = NumberVerificationRepository::class.simpleName
    }
}
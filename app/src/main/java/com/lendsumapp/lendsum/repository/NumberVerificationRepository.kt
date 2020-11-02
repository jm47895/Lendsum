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
    @ActivityContext private val  context: Context
) {

    private val remoteUserObject: MutableLiveData<User> = MutableLiveData()

    //Cache functions
    suspend fun insertUserIntoSqlCache(user: User){
        cacheDb.getUserDao().insertUser(user)
    }

    fun doesDbCacheExist(dbName: String): Boolean{
        return DatabaseUtils.doesCacheDatabaseExist(context, dbName)
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

    fun getExistingUserFromFirestore(uid: String){

        firestoreDb.collection(USER_COLLECTION_PATH)
            .document(uid)
            .get().addOnSuccessListener { document ->
                if(document != null){
                    Log.d(TAG, "Document Exists")
                    remoteUserObject.postValue( document.toObject<User>())
                }else{
                    Log.d(TAG, "Document Does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, exception.toString())
            }

    }

    fun getRemoteUser(): MutableLiveData<User> {
        return remoteUserObject
    }
    //End firestore functions

    companion object{
        private val TAG = NumberVerificationRepository::class.simpleName
    }
}
package com.lendsumapp.lendsum.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants.USER_COLLECTION_PATH
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ProfileRepository @Inject constructor(
    private val database: LendsumDatabase,
    private var firebaseAuth: FirebaseAuth?,
    private val firestoreDb: FirebaseFirestore
){

    private val remoteUser: MutableLiveData<User> = MutableLiveData()

    fun getFirebaseDisplayName(): String{
        return firebaseAuth?.currentUser?.displayName.toString()
    }

    suspend fun getCachedUser(userId: String): User {
        return database.getUserDao().getUser(userId)
    }

    fun requestRemoteUser(uid: String){

        firestoreDb.collection(USER_COLLECTION_PATH)
            .document(uid)
            .get().addOnSuccessListener { document ->
                if(document != null){
                    Log.d(TAG, "Document Exists")
                    remoteUser.postValue( document.toObject<User>())
                }else{
                    Log.d(TAG, "Document Does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, exception.toString())
            }

    }

    fun getRemoteUser(): MutableLiveData<User>{
        return remoteUser
    }

    companion object{
        private val TAG = ProfileRepository::class.simpleName
    }

}
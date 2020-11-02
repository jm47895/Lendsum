package com.lendsumapp.lendsum.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lendsumapp.lendsum.auth.EmailAndPassAuthComponent
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class EditProfileRepository @Inject constructor(
    private val lendsumDatabase: LendsumDatabase,
    private val emailAndPassAuthComponent: EmailAndPassAuthComponent,
    private val firestoreDb: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
){

    suspend fun getCachedUser(userId:String): User{
        return lendsumDatabase.getUserDao().getUser(userId)
    }

    suspend fun updateCachedUser(user: User): Int{
        return lendsumDatabase.getUserDao().updateUser(user)
    }

    suspend fun updateAuthEmail(email: String){
        emailAndPassAuthComponent.updateAuthEmail(email)
    }

    fun getUpdateAuthEmailStatus(): MutableLiveData<Boolean> {
        return emailAndPassAuthComponent.getUpdateAuthEmailStatus()
    }

    suspend fun updateAuthPass(password: String){
        emailAndPassAuthComponent.updateAuthPassword(password)
    }

    fun getUpdateAuthPassStatus(): MutableLiveData<Boolean>{
        return emailAndPassAuthComponent.getUpdateAuthPassStatus()
    }

    fun updateFirebaseAuthProfile(key: String, value: String){
        emailAndPassAuthComponent.updateFirebaseAuthProfile(key, value)
    }

    fun updateUserValueInFirestore(key: String, stringValue: String?, booleanValue: Boolean?){
        val userDoc = firestoreDb.collection(GlobalConstants.USER_COLLECTION_PATH)
            .document(firebaseAuth.currentUser?.uid.toString())

        stringValue?.let {
            userDoc.update(key, it).addOnCompleteListener { task->
                if(task.isSuccessful){
                    Log.d(TAG, "$key updated in firestore")
                }else{
                    Log.d(TAG, "$key failed to update in firestore")
                }
            }
        }

        booleanValue?.let {
            userDoc.update(key, it).addOnCompleteListener { task->
                if(task.isSuccessful){
                    Log.d(TAG, "$key updated in firestore")
                }else{
                    Log.d(TAG, "$key failed to update in firestore")
                }
            }
        }

    }

    companion object {
        private val TAG = EditProfileRepository::class.simpleName
    }
}
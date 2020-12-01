package com.lendsumapp.lendsum.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.lendsumapp.lendsum.auth.EmailAndPassAuthComponent
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_PROFILE_PIC_URI_KEY
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditProfileRepository @Inject constructor(
    private val lendsumDatabase: LendsumDatabase,
    private val emailAndPassAuthComponent: EmailAndPassAuthComponent,
    private val firestoreDb: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorageReference: StorageReference
){

    fun getCachedUser(userId:String): Flow<User> {
        return lendsumDatabase.getUserDao().getUser(userId)
    }

    suspend fun updateLocalCachedUser(user: User): Int{
        return lendsumDatabase.getUserDao().updateUser(user)
    }

    fun updateAuthEmail(email: String){
        emailAndPassAuthComponent.updateAuthEmail(email)
    }

    fun getUpdateAuthEmailStatus(): MutableLiveData<Boolean> {
        return emailAndPassAuthComponent.getUpdateAuthEmailStatus()
    }

    fun updateAuthPass(password: String){
        emailAndPassAuthComponent.updateAuthPassword(password)
    }

    fun getUpdateAuthPassStatus(): MutableLiveData<Boolean>{
        return emailAndPassAuthComponent.getUpdateAuthPassStatus()
    }

    fun updateFirebaseAuthProfile(key: String, value: String){
        emailAndPassAuthComponent.updateFirebaseAuthProfile(key, value)
    }

    fun updateUserValueInFirestore(key: String, stringValue: String?, booleanValue: Boolean?){
        val userDoc = firestoreDb.collection(GlobalConstants.FIRESTORE_USER_COLLECTION_PATH)
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

    fun uploadProfilePhotoToFirebaseStorage(fileName: String, uri: Uri){

        val profileImageRef = firebaseStorageReference.child("profile_pics").child(fileName)

        val uploadTask = profileImageRef.putFile(uri)
        uploadTask.addOnCompleteListener{ task->
            if(task.isSuccessful){
                Log.d(TAG, "Profile pic uploaded to storage")
                updateProfPicUriInFirebaseDbs(profileImageRef)
            }else{
                Log.d(TAG, "Profile pic failed to upload to storage ${task.exception}")
            }
        }
    }

    private fun updateProfPicUriInFirebaseDbs(profileImageRef: StorageReference) {
        profileImageRef.downloadUrl.addOnSuccessListener {
            updateFirebaseAuthProfile(FIRESTORE_PROFILE_PIC_URI_KEY, it.toString())
            updateUserValueInFirestore(FIRESTORE_PROFILE_PIC_URI_KEY, it.toString(), null)
        }.addOnFailureListener {
            Log.d(TAG, "Prof pic url failed to download: $it")
        }
    }

    companion object {
        private val TAG = EditProfileRepository::class.simpleName
    }
}
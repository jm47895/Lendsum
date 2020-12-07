package com.lendsumapp.lendsum.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.lendsumapp.lendsum.auth.EmailAndPassAuthComponent
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_PROFILE_PIC_URI_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_COLLECTION_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.PROF_IMAGE_STORAGE_WORK_NAME
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import com.lendsumapp.lendsum.workers.RetrieveRemoteImageUriWorker
import com.lendsumapp.lendsum.workers.UploadImageToDataDirectoryWorker
import com.lendsumapp.lendsum.workers.UploadImageToFirebaseStorageWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditProfileRepository @Inject constructor(
    private val lendsumDatabase: LendsumDatabase,
    private val emailAndPassAuthComponent: EmailAndPassAuthComponent,
    private val firestoreDb: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
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
        val userDoc = firestoreDb.collection(FIRESTORE_USER_COLLECTION_PATH)
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

    fun launchUploadImageWorkers(fileName: String, uri: Uri){

        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val uploadImgToLocalData = OneTimeWorkRequestBuilder<UploadImageToDataDirectoryWorker>()
            .setInputData(createFileNameAndUriData(fileName, uri))
            .build()

        val uploadImgToFirebaseStorage = OneTimeWorkRequestBuilder<UploadImageToFirebaseStorageWorker>()
            .setInputData(createFileNameAndUriData(fileName, uri))
            .setConstraints(constraints)
            .build()

        val retrieveRemoteImgUri = OneTimeWorkRequestBuilder<RetrieveRemoteImageUriWorker>()
            .setInputData(createFileNameAndUriData(fileName, uri))
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().beginUniqueWork(PROF_IMAGE_STORAGE_WORK_NAME, ExistingWorkPolicy.REPLACE,
            uploadImgToLocalData).then(uploadImgToFirebaseStorage).then(retrieveRemoteImgUri).enqueue()
    }

    private fun createFileNameAndUriData(fileName: String, uri: Uri): Data {
        return Data.Builder()
            .putString(UPLOAD_PROF_PIC_NAME_KEY,fileName)
            .putString(UPLOAD_PROF_PIC_URI_KEY, uri.toString())
            .build()
    }

    companion object {
        private val TAG = EditProfileRepository::class.simpleName
    }
}
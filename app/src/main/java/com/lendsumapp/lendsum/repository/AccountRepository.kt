package com.lendsumapp.lendsum.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.lendsumapp.lendsum.auth.EmailAndPassAuthComponent
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_AUTH_UPDATE_MAP_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_AUTH_UPDATE_MAP_VALUE
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_WORKER_MAP_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_WORKER_MAP_VALUE
import com.lendsumapp.lendsum.util.GlobalConstants.PROF_IMAGE_STORAGE_WORK_NAME
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import com.lendsumapp.lendsum.workers.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val lendsumDatabase: LendsumDatabase,
    private val emailAndPassAuthComponent: EmailAndPassAuthComponent
){
    val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

    fun getCachedUser(userId:String): Flow<User?> {
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
        val updateFirebaseAuthProfile = OneTimeWorkRequestBuilder<UpdateFirebaseAuthProfileWorker>()
            .setConstraints(constraints)
            .setInputData(createFirebaseAuthData(key, value))
            .build()

        WorkManager.getInstance().enqueue(updateFirebaseAuthProfile)
    }

    private fun createFirebaseAuthData(key: String, value: String): Data {
        return Data.Builder()
            .putString(FIREBASE_AUTH_UPDATE_MAP_KEY,key)
            .putString(FIREBASE_AUTH_UPDATE_MAP_VALUE, value)
            .build()
    }

    fun launchUpdateFirestoreUserValueWorker(key: String, value: Any){

        val updateUserValueInFirestore = OneTimeWorkRequestBuilder<UpdateUserValueInFirestoreWorker>()
            .setConstraints(constraints)
            .setInputData(createUpdateUserFirestoreData(key, value))
            .build()

        WorkManager.getInstance().enqueue(updateUserValueInFirestore)
    }

    private fun createUpdateUserFirestoreData(key: String, value: Any): Data {

        val dataBuilder = Data.Builder().putString(FIRESTORE_USER_WORKER_MAP_KEY, key)

        if (value is Boolean){
            dataBuilder.putBoolean(FIRESTORE_USER_WORKER_MAP_VALUE, value)
        }else if(value is String){
            dataBuilder.putString(FIRESTORE_USER_WORKER_MAP_VALUE, value)
        }

        return dataBuilder.build()
    }

    fun launchUploadImageWorkers(fileName: String, uri: Uri){

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

        val uploadImgToFirestore = OneTimeWorkRequestBuilder<UploadImgUriToFirestoreWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().beginUniqueWork(PROF_IMAGE_STORAGE_WORK_NAME, ExistingWorkPolicy.REPLACE,
            uploadImgToLocalData).then(uploadImgToFirebaseStorage).then(retrieveRemoteImgUri).then(uploadImgToFirestore).enqueue()
    }

    private fun createFileNameAndUriData(fileName: String, uri: Uri): Data {
        return Data.Builder()
            .putString(UPLOAD_PROF_PIC_NAME_KEY,fileName)
            .putString(UPLOAD_PROF_PIC_URI_KEY, uri.toString())
            .build()
    }

    companion object {
        private val TAG = AccountRepository::class.simpleName
    }
}
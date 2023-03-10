package com.lendsumapp.lendsum.repository

import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.lendsumapp.lendsum.auth.EmailAndPassAuthComponent
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_AUTH_UPDATE_MAP_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_AUTH_UPDATE_MAP_VALUE
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_EMAIL_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_PROFILE_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_PROFILE_PIC_URI_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_USERNAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_WORKER_MAP_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_WORKER_MAP_VALUE
import com.lendsumapp.lendsum.util.GlobalConstants.PROF_IMAGE_STORAGE_WORK_NAME
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import com.lendsumapp.lendsum.workers.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val lendsumDatabase: LendsumDatabase,
    private val emailAndPassAuthComponent: EmailAndPassAuthComponent,
    private val workManager: WorkManager
){
    private val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

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

    fun updateFirebaseAuthProfile(user: User): UUID{

        val updateFirebaseAuthProfileRequest = OneTimeWorkRequestBuilder<UpdateFirebaseAuthProfileWorker>()
            .setConstraints(constraints)
            .setInputData(
                Data.Builder()
                    .putString(FIREBASE_PROFILE_NAME_KEY, user.name)
                    .putString(FIREBASE_PROFILE_PIC_URI_KEY, user.profilePicUri)
                    .build()
            )
            .build()

        workManager.enqueue(updateFirebaseAuthProfileRequest)

        return updateFirebaseAuthProfileRequest.id
    }

    fun launchUpdateFirestoreUserValueWorker(user: User): UUID{

        val updateUserInFirestore = OneTimeWorkRequestBuilder<UpdateUserValueInFirestoreWorker>()
            .setConstraints(constraints)
            .setInputData(
                Data.Builder()
                    .putString(FIREBASE_PROFILE_NAME_KEY, user.name)
                    .putString(FIREBASE_EMAIL_KEY, user.email)
                    .putString(FIREBASE_USERNAME_KEY, user.username)
                    .build()
            )
            .build()

        workManager.enqueue(updateUserInFirestore)

        return updateUserInFirestore.id
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

        workManager.beginUniqueWork(PROF_IMAGE_STORAGE_WORK_NAME, ExistingWorkPolicy.REPLACE,
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
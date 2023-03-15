package com.lendsumapp.lendsum.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.lendsumapp.lendsum.auth.EmailAndPassAuthComponent
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_PROFILE_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_PROFILE_PIC_URI_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_USERNAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.PROF_IMAGE_STORAGE_WORK_NAME
import com.lendsumapp.lendsum.util.GlobalConstants.UPDATE_USER_PROF_KEY
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

    fun launchUpdateProfileWorker(user: User): UUID{

        val updateFirebaseAuthProfileRequest = OneTimeWorkRequestBuilder<UpdateFirebaseAuthProfileWorker>()
            .setConstraints(constraints)
            .setInputData(
                Data.Builder()
                    .putString(FIREBASE_PROFILE_NAME_KEY, user.name)
                    .putString(FIREBASE_PROFILE_PIC_URI_KEY, user.profilePicUri)
                    .build()
            )
            .build()

        val updateUserInFirestore = OneTimeWorkRequestBuilder<UpdateUserFirestoreWorker>()
            .setConstraints(constraints)
            .setInputData(
                Data.Builder()
                    .putString(FIREBASE_PROFILE_NAME_KEY, user.name)
                    .putString(FIREBASE_USERNAME_KEY, user.username)
                    .build()
            )
            .build()

        workManager.beginUniqueWork(UPDATE_USER_PROF_KEY, ExistingWorkPolicy.REPLACE, updateFirebaseAuthProfileRequest)
            .then(updateUserInFirestore)
            .enqueue()

        return updateUserInFirestore.id
    }

    fun launchUploadImageWorkers(fileName: String, uri: Uri): UUID{

        val uploadImgToLocalData = OneTimeWorkRequestBuilder<UploadImageToDataDirectoryWorker>()
            .setInputData(createFileNameAndUriData(fileName, uri))
            .build()

        val uploadImgUriToFirebaseStorage = OneTimeWorkRequestBuilder<UploadImageToFirebaseStorageWorker>()
            .setInputData(createFileNameAndUriData(fileName, uri))
            .setConstraints(constraints)
            .build()

        val retrieveRemoteImgUri = OneTimeWorkRequestBuilder<RetrieveRemoteImageUriWorker>()
            .setInputData(createFileNameAndUriData(fileName, uri))
            .setConstraints(constraints)
            .build()

        val uploadImgUriToFirestore = OneTimeWorkRequestBuilder<UploadImgUriToFirestoreWorker>()
            .setConstraints(constraints)
            .build()

        workManager.beginUniqueWork(PROF_IMAGE_STORAGE_WORK_NAME, ExistingWorkPolicy.REPLACE,
            uploadImgToLocalData).then(uploadImgUriToFirebaseStorage).then(retrieveRemoteImgUri).then(uploadImgUriToFirestore).enqueue()

        return uploadImgUriToFirestore.id
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
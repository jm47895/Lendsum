package com.lendsumapp.lendsum.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.storage.StorageReference
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_STORAGE_PROFILE_PIC_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltWorker
class UploadImageToFirebaseStorageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val firebaseStorage: StorageReference
) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {

        if (runAttemptCount == 3){
            return Result.failure()
        }

        return suspendCoroutine { continuation ->

            try {

                val fileName = inputData.getString(UPLOAD_PROF_PIC_NAME_KEY)
                val imageUri = inputData.getString(UPLOAD_PROF_PIC_URI_KEY)

                val profileImageRef = fileName?.let {
                    firebaseStorage.child(FIREBASE_STORAGE_PROFILE_PIC_PATH).child(it)
                }

                val uploadTask = profileImageRef?.putFile(Uri.parse(imageUri))
                uploadTask?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i(TAG, "Profile pic uploaded to storage")
                        continuation.resume(Result.success())
                    } else {
                        Log.e(TAG, "Profile pic failed to upload to storage ${task.exception}")
                        continuation.resume(Result.retry())
                    }
                } ?: continuation.resume(Result.retry())

            }catch (e: Exception){
                continuation.resume(Result.retry())
            }

        }
    }

    companion object{
        private val TAG = UploadImageToFirebaseStorageWorker::class.java.simpleName
    }
}
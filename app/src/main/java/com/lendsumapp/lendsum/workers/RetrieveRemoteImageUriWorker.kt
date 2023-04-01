package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.storage.StorageReference
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltWorker
class RetrieveRemoteImageUriWorker @AssistedInject constructor(
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
                val profileImageRef = fileName?.let {
                    firebaseStorage.child(GlobalConstants.FIREBASE_STORAGE_PROFILE_PIC_PATH)
                        .child(it)
                }

                profileImageRef?.downloadUrl?.addOnCompleteListener {
                    if (it.isSuccessful){
                        Log.i(TAG, "Prof pic url success: ${it.result}")
                        val data: Data = workDataOf(UPLOAD_PROF_PIC_URI_KEY to it.result.toString())
                        continuation.resume(Result.success(data))
                    }else{
                        Log.e(TAG, "Prof pic url failed to download: ${it.exception}")
                        continuation.resume(Result.retry())
                    }
                } ?: continuation.resume(Result.retry())

            }catch (e: Exception){
                continuation.resume(Result.retry())
            }
        }
    }

    companion object {
        private val TAG = RetrieveRemoteImageUriWorker::class.simpleName
    }
}
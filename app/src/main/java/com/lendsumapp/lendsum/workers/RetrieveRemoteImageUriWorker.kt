package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import kotlinx.coroutines.delay
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.suspendCoroutine

class RetrieveRemoteImageUriWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {

        if (runAttemptCount == 3){
            return Result.failure()
        }

        return suspendCoroutine { continuation ->

            try {

                val firebaseStorageReference = Firebase.storage.reference
                val fileName = inputData.getString(UPLOAD_PROF_PIC_NAME_KEY)
                val profileImageRef = fileName?.let {
                    firebaseStorageReference.child(GlobalConstants.FIREBASE_STORAGE_PROFILE_PIC_PATH)
                        .child(it)
                }

                profileImageRef?.downloadUrl?.addOnCompleteListener {
                    if (it.isSuccessful){
                        Log.d(TAG, "Prof pic url success: ${it.result}")
                        val data: Data = workDataOf(UPLOAD_PROF_PIC_URI_KEY to it.result.toString())
                        continuation.resumeWith(kotlin.Result.success(Result.success(data)))
                    }else{
                        Log.d(TAG, "Prof pic url failed to download: ${it.exception}")
                        continuation.resumeWith(kotlin.Result.success(Result.retry()))
                    }
                } ?: continuation.resumeWith(kotlin.Result.success(Result.retry()))

            }catch (e: Exception){
                continuation.resumeWith(kotlin.Result.success(Result.retry()))
            }
        }
    }

    companion object {
        private val TAG = RetrieveRemoteImageUriWorker::class.simpleName
    }
}
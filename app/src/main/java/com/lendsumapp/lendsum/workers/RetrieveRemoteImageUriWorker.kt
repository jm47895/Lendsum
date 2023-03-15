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

class RetrieveRemoteImageUriWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {

        var result = Result.failure()
        val firebaseStorageReference = Firebase.storage.reference
        val fileName = inputData.getString(UPLOAD_PROF_PIC_NAME_KEY)
        val profileImageRef = fileName?.let { firebaseStorageReference.child(GlobalConstants.FIREBASE_STORAGE_PROFILE_PIC_PATH).child(it) }

        profileImageRef?.downloadUrl?.addOnSuccessListener {
            Log.d(TAG, "Prof pic url success: $it")
            val data: Data = workDataOf(UPLOAD_PROF_PIC_URI_KEY to it.toString())
            result = Result.success(data)

        }?.addOnFailureListener {
            Log.d(TAG, "Prof pic url failed to download: $it")
            result = Result.failure()

        }

        delay(1000)

        return result
    }

    companion object {
        private val TAG = RetrieveRemoteImageUriWorker::class.simpleName
    }
}
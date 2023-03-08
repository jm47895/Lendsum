package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import java.util.concurrent.CountDownLatch

class RetrieveRemoteImageUriWorker(context: Context, params: WorkerParameters) : Worker(context, params){
    override fun doWork(): Result {

        val latch = CountDownLatch(1)
        var result = Result.failure()
        val firebaseStorageReference = Firebase.storage.reference
        val fileName = inputData.getString(UPLOAD_PROF_PIC_NAME_KEY)
        val profileImageRef = fileName?.let { firebaseStorageReference.child(GlobalConstants.FIREBASE_STORAGE_PROFILE_PIC_PATH).child(it) }

        profileImageRef?.downloadUrl?.addOnSuccessListener {
            Log.d(TAG, "Prof pic url success: $it")
            val data: Data = workDataOf(UPLOAD_PROF_PIC_URI_KEY to it.toString())
            result = Result.success(data)
            latch.countDown()
        }?.addOnFailureListener {
            Log.d(TAG, "Prof pic url failed to download: $it")
            result = Result.failure()
            latch.countDown()
        }
        latch.await()
        return result
    }

    companion object {
        private val TAG = RetrieveRemoteImageUriWorker::class.simpleName
    }
}
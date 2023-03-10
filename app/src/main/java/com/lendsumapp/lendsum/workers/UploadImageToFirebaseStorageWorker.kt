package com.lendsumapp.lendsum.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_STORAGE_PROFILE_PIC_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import java.util.concurrent.CountDownLatch

class UploadImageToFirebaseStorageWorker(context: Context, params: WorkerParameters) : Worker(context, params){
    override fun doWork(): Result {
        //This makes sure on complete listener finishes before triggering UploadImageWorker Result.success callback
        val latch = CountDownLatch(1)
        var result = Result.failure()
        val firebaseStorageReference = Firebase.storage.reference
        val fileName = inputData.getString(UPLOAD_PROF_PIC_NAME_KEY)
        val imageUri = inputData.getString(UPLOAD_PROF_PIC_URI_KEY)

        val profileImageRef = fileName?.let {
            firebaseStorageReference.child(FIREBASE_STORAGE_PROFILE_PIC_PATH).child(it)
            }

        val uploadTask = profileImageRef?.putFile(Uri.parse(imageUri))
        uploadTask?.addOnCompleteListener{ task->
            if(task.isSuccessful){
                Log.i(TAG, "Profile pic uploaded to storage")
                result = Result.success()
                latch.countDown()
            }else{
                Log.e(TAG, "Profile pic failed to upload to storage ${task.exception}")
                result = Result.failure()
                latch.countDown()
            }
        }
        latch.await()
        return result
    }

    companion object{
        private val TAG = UploadImageToFirebaseStorageWorker::class.java.simpleName
    }
}
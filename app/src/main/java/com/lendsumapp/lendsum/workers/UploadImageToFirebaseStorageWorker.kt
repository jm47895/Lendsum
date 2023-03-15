package com.lendsumapp.lendsum.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_STORAGE_PROFILE_PIC_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import kotlinx.coroutines.delay
import java.util.concurrent.CountDownLatch

class UploadImageToFirebaseStorageWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {

        var result = Result.failure()
        val firebaseStorageReference = Firebase.storage.reference
        val fileName = inputData.getString(UPLOAD_PROF_PIC_NAME_KEY)
        val imageUri = inputData.getString(UPLOAD_PROF_PIC_URI_KEY)

        val profileImageRef = fileName?.let {
            firebaseStorageReference.child(FIREBASE_STORAGE_PROFILE_PIC_PATH).child(it)
            }

        val uploadTask = profileImageRef?.putFile(Uri.parse(imageUri))
        uploadTask?.addOnCompleteListener{ task->
            result = if(task.isSuccessful){
                Log.i(TAG, "Profile pic uploaded to storage")
                Result.success()
            }else{
                Log.e(TAG, "Profile pic failed to upload to storage ${task.exception}")
                Result.failure()
            }
        }

        delay(1000)

        return result
    }

    companion object{
        private val TAG = UploadImageToFirebaseStorageWorker::class.java.simpleName
    }
}
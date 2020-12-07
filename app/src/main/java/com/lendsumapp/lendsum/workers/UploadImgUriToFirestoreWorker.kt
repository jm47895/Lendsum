package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_PROFILE_PIC_URI_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_COLLECTION_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import java.util.concurrent.CountDownLatch

class UploadImgUriToFirestoreWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {

        val latch = CountDownLatch(1)
        var result = Result.failure()
        val firestoreDb = Firebase.firestore
        val firebaseAuth = FirebaseAuth.getInstance()
        val uri = inputData.getString(UPLOAD_PROF_PIC_URI_KEY)

        val userDoc = firestoreDb.collection(FIRESTORE_USER_COLLECTION_PATH)
            .document(firebaseAuth.currentUser?.uid.toString())

        userDoc.update(FIRESTORE_PROFILE_PIC_URI_KEY, uri).addOnCompleteListener { task->
            if(task.isSuccessful){
                Log.d(TAG, "Profile pic uri update success")
                result = Result.success()
                latch.countDown()
            }else{
                Log.d(TAG, "Profile pic uri update failed: " + task.exception)
                result = Result.failure()
                latch.countDown()
            }
        }
        latch.await()

        return result
    }

    companion object{
        private val TAG = UploadImgUriToFirestoreWorker::class.java.simpleName
    }
}
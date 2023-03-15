package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_PROFILE_PIC_URI_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_USER_COLLECTION_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch

class UploadImgUriToFirestoreWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {

        var result = Result.failure()
        val firestoreDb = Firebase.firestore
        val firebaseAuth = FirebaseAuth.getInstance()
        val uri = inputData.getString(UPLOAD_PROF_PIC_URI_KEY)

        val userDoc = firestoreDb.collection(FIREBASE_USER_COLLECTION_PATH)
            .document(firebaseAuth.currentUser?.uid.toString())

        userDoc.update(FIREBASE_PROFILE_PIC_URI_KEY, uri).addOnCompleteListener { task->
            result = if(task.isSuccessful){
                Log.d(TAG, "Profile pic uri update success")
                Result.success()
            }else{
                Log.d(TAG, "Profile pic uri update failed: " + task.exception)
                Result.failure()
            }
        }

        delay(1000)

        return result
    }

    companion object{
        private val TAG = UploadImgUriToFirestoreWorker::class.java.simpleName
    }
}
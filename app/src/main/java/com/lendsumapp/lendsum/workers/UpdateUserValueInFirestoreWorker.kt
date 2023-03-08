package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_WORKER_MAP_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_WORKER_MAP_VALUE
import java.util.concurrent.CountDownLatch

class UpdateUserValueInFirestoreWorker(context: Context, params: WorkerParameters) : Worker(context, params){
    override fun doWork(): Result {

        val latch = CountDownLatch(1)
        var result = Result.failure()
        val firestoreDb = Firebase.firestore
        val firebaseAuth = FirebaseAuth.getInstance()
        val key = inputData.getString(FIRESTORE_USER_WORKER_MAP_KEY)
        val booleanValue = inputData.getBoolean(FIRESTORE_USER_WORKER_MAP_VALUE, true)
        val value = inputData.getString(FIRESTORE_USER_WORKER_MAP_VALUE) ?: booleanValue

        val userDoc = firestoreDb.collection(GlobalConstants.FIREBASE_USER_COLLECTION_PATH)
            .document(firebaseAuth.currentUser?.uid.toString())

        userDoc.update(key.toString(), value).addOnCompleteListener { task->
            if(task.isSuccessful){
                Log.d(TAG, "$key updated in firestore")
                result = Result.success()
                latch.countDown()
            }else{
                Log.d(TAG, "$key failed to update in firestore")
                result = Result.failure()
                latch.countDown()
            }
        }
        latch.await()

        return result
    }

    companion object{
        private val TAG = UpdateUserValueInFirestoreWorker::class.java.simpleName
    }
}
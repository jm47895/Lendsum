package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_WORKER_MAP_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_USER_WORKER_MAP_VALUE
import kotlinx.coroutines.delay
import java.util.concurrent.CountDownLatch

class UpdateUserValueInFirestoreWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params){

    override suspend fun doWork(): Result {

        var result = Result.failure()
        val firestoreDb = Firebase.firestore
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        firebaseUser?.let { user ->
            val userDoc = firestoreDb.collection(GlobalConstants.FIREBASE_USER_COLLECTION_PATH)
                .document(user.uid)

            userDoc.update(inputData.keyValueMap).addOnCompleteListener { task->
                result = if(task.isSuccessful){
                    Log.i(TAG, "User document updated in firestore")
                    Result.success()
                }else{
                    Log.e(TAG, "User document failed to update in firestore")
                    Result.failure()

                }
            }
        } ?: return Result.failure()

        delay(1000)

        return result
    }

    companion object{
        private val TAG = UpdateUserValueInFirestoreWorker::class.java.simpleName
    }
}
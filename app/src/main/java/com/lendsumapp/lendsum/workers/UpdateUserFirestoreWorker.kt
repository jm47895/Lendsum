package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lendsumapp.lendsum.util.GlobalConstants
import kotlin.coroutines.suspendCoroutine

class UpdateUserFirestoreWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {

        if (runAttemptCount == 3){
            return Result.failure()
        }

         return suspendCoroutine{ continuation ->

            try {
                val firestoreDb = Firebase.firestore
                val firebaseUser = FirebaseAuth.getInstance().currentUser


                firebaseUser?.let { user ->
                    val userDoc = firestoreDb.collection(GlobalConstants.FIREBASE_USER_COLLECTION_PATH)
                        .document(user.uid)

                    userDoc.update(inputData.keyValueMap).addOnCompleteListener { task->
                        if(task.isSuccessful){
                            Log.i(TAG, "User document updated in firestore")
                            continuation.resumeWith(kotlin.Result.success(Result.success()))
                        }else{
                            Log.e(TAG, "User document failed to update in firestore")
                            continuation.resumeWith(kotlin.Result.success(Result.retry()))
                        }
                    }
                } ?: continuation.resumeWith(kotlin.Result.success(Result.retry()))
            }catch (e: Exception){
                continuation.resumeWith(kotlin.Result.success(Result.retry()))
            }
        }
    }

    companion object{
        private val TAG = UpdateUserFirestoreWorker::class.java.simpleName
    }
}
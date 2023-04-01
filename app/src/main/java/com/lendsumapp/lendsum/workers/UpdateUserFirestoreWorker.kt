package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lendsumapp.lendsum.util.GlobalConstants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltWorker
class UpdateUserFirestoreWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val firestoreDb: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {

        if (runAttemptCount == 3){
            return Result.failure()
        }

         return suspendCoroutine{ continuation ->

            try {

                val firebaseUser = firebaseAuth.currentUser


                firebaseUser?.let { user ->
                    val userDoc = firestoreDb.collection(GlobalConstants.FIREBASE_USER_COLLECTION_PATH)
                        .document(user.uid)

                    userDoc.update(inputData.keyValueMap).addOnCompleteListener { task->
                        if(task.isSuccessful){
                            Log.i(TAG, "User document updated in firestore")
                            continuation.resume(Result.success())
                        }else{
                            Log.e(TAG, "User document failed to update in firestore")
                            continuation.resume(Result.retry())
                        }
                    }
                } ?: continuation.resume(Result.retry())

            }catch (e: Exception){
                continuation.resume(Result.retry())
            }
        }
    }

    companion object{
        private val TAG = UpdateUserFirestoreWorker::class.java.simpleName
    }
}
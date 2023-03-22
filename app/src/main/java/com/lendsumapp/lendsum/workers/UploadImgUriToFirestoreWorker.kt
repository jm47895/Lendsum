package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_PROFILE_PIC_URI_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_USER_COLLECTION_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.coroutines.suspendCoroutine
@HiltWorker
class UploadImgUriToFirestoreWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val firestoreDb: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {

        if (runAttemptCount == 3){
            return Result.failure()
        }

        return suspendCoroutine { continuation ->

            try {

                val firebaseUser = firebaseAuth.currentUser
                val uri = inputData.getString(UPLOAD_PROF_PIC_URI_KEY)

                firebaseUser?.let {
                    val userDoc = firestoreDb.collection(FIREBASE_USER_COLLECTION_PATH)
                        .document(firebaseAuth.currentUser?.uid.toString())

                    userDoc.update(FIREBASE_PROFILE_PIC_URI_KEY, uri).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i(TAG, "Profile pic uri update success")
                            continuation.resumeWith(kotlin.Result.success(Result.success()))
                        } else {
                            Log.e(TAG, "Profile pic uri update failed: " + task.exception)
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
        private val TAG = UploadImgUriToFirestoreWorker::class.java.simpleName
    }
}
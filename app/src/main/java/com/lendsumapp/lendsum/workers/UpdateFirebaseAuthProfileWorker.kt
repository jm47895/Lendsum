package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_AUTH_UPDATE_MAP_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_AUTH_UPDATE_MAP_VALUE
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_PROFILE_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_PROFILE_PIC_URI_KEY
import kotlinx.coroutines.delay
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.suspendCoroutine

class UpdateFirebaseAuthProfileWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        if (runAttemptCount == 3){
            return Result.failure()
        }

        return suspendCoroutine { continuation ->

            try{

                val currentUser = FirebaseAuth.getInstance().currentUser
                val displayName = inputData.getString(FIREBASE_PROFILE_NAME_KEY)
                val profilePicUri = inputData.getString(FIREBASE_PROFILE_PIC_URI_KEY)

                val changeRequest =
                    UserProfileChangeRequest.Builder().setPhotoUri(profilePicUri?.toUri())
                        .setDisplayName(displayName).build()

                currentUser?.updateProfile(changeRequest)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i(TAG, "Firebase auth profile updated DisplayName:${currentUser.displayName} $profilePicUri")
                        continuation.resumeWith(kotlin.Result.success(Result.success()))
                    } else {
                        Log.e(TAG, "Firebase auth failed to update. ${task.exception}")
                        continuation.resumeWith(kotlin.Result.success(Result.retry()))
                    }
                } ?: continuation.resumeWith(kotlin.Result.success(Result.retry()))

            }catch (e: Exception){
                continuation.resumeWith(kotlin.Result.success(Result.retry()))
            }
        }
    }

    companion object{
        private val TAG = UpdateFirebaseAuthProfileWorker::class.java.simpleName
    }
}
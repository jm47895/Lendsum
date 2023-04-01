package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_PROFILE_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_PROFILE_PIC_URI_KEY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltWorker
class UpdateFirebaseAuthProfileWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val firebaseAuth: FirebaseAuth
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        if (runAttemptCount == 3){
            return Result.failure()
        }

        return suspendCoroutine { continuation ->

            try{

                val currentUser = firebaseAuth.currentUser
                val displayName = inputData.getString(FIREBASE_PROFILE_NAME_KEY)
                val profilePicUri = inputData.getString(FIREBASE_PROFILE_PIC_URI_KEY)

                val changeRequest =
                    UserProfileChangeRequest.Builder().setPhotoUri(profilePicUri?.toUri())
                        .setDisplayName(displayName).build()

                currentUser?.updateProfile(changeRequest)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i(TAG, "Firebase auth profile updated DisplayName:${currentUser.displayName} $profilePicUri")
                        continuation.resume(Result.success())
                    } else {
                        Log.e(TAG, "Firebase auth failed to update. ${task.exception}")
                        continuation.resume(Result.retry())
                    }
                } ?: continuation.resume(Result.retry())

            }catch (e: Exception){
                continuation.resume(Result.retry())
            }
        }
    }

    companion object{
        private val TAG = UpdateFirebaseAuthProfileWorker::class.java.simpleName
    }
}
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

class UpdateFirebaseAuthProfileWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        var result = Result.failure()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val displayName = inputData.getString(FIREBASE_PROFILE_NAME_KEY)
        val profilePicUri = inputData.getString(FIREBASE_PROFILE_PIC_URI_KEY)

        val changeRequest = UserProfileChangeRequest.Builder().setPhotoUri(profilePicUri?.toUri()).setDisplayName(displayName).build()

        currentUser?.updateProfile(changeRequest)?.addOnCompleteListener { task ->
            result = if(task.isSuccessful){
                Log.i(TAG, "Firebase auth profile updated DisplayName:${currentUser.displayName} $profilePicUri")
                Result.success()
            }else{
                Log.e(TAG, "Firebase auth failed to update. ${task.exception}")
                Result.failure()
            }
        } ?: return Result.failure()

        delay(1000)

        return result
    }

    companion object{
        private val TAG = UpdateFirebaseAuthProfileWorker::class.java.simpleName
    }
}
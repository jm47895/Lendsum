package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_AUTH_UPDATE_MAP_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_AUTH_UPDATE_MAP_VALUE
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_PROFILE_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIRESTORE_PROFILE_PIC_URI_KEY
import java.util.concurrent.CountDownLatch

class UpdateFirebaseAuthProfileWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {

        val latch = CountDownLatch(1)
        var result = Result.failure()
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        val key = inputData.getString(FIREBASE_AUTH_UPDATE_MAP_KEY)
        val value = inputData.getString(FIREBASE_AUTH_UPDATE_MAP_VALUE)

        when(key){
            FIRESTORE_PROFILE_NAME_KEY -> {
                UserProfileChangeRequest.Builder().setDisplayName(value).build()
            }
            FIRESTORE_PROFILE_PIC_URI_KEY -> {
                UserProfileChangeRequest.Builder().setPhotoUri(value?.toUri()).build()
            }
            else -> null
        }.let { profChangeRequest->
            profChangeRequest?.let {
                currentUser.updateProfile(it).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        Log.d(TAG, "Firebase auth $key updated to ${currentUser.displayName}")
                        result = Result.success()
                        latch.countDown()
                    }else{
                        Log.d(TAG, "Firebase auth failed to update $key.")
                        result = Result.failure()
                        latch.countDown()
                    }
                }
            }
        }
        latch.await()

        return result
    }

    companion object{
        private val TAG = UpdateFirebaseAuthProfileWorker::class.java.simpleName
    }
}
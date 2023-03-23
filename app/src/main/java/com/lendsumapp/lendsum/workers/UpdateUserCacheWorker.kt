package com.lendsumapp.lendsum.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lendsumapp.lendsum.data.persistence.Converters.toUserObject
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants.UPDATE_USER_CACHE_WORKER_KEY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateUserCacheWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val cacheDb: LendsumDatabase
) : CoroutineWorker(context, params){

    override suspend fun doWork(): Result {

        if (runAttemptCount == 3){
            return Result.failure()
        }

        return try{
            val user = inputData.getString(UPDATE_USER_CACHE_WORKER_KEY)?.toUserObject()

            user?.let { cacheDb.getUserDao().updateUser(it) }

            Log.i(TAG, "User updated in cache")

            Result.success()
        }catch (e: Exception){
            Log.e(TAG, "User failed to update in cache: $e")
            Result.failure()
        }
    }

    companion object{
        private val TAG = UpdateUserCacheWorker::class.java.simpleName
    }
}
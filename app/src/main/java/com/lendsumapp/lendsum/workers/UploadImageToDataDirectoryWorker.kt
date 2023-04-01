package com.lendsumapp.lendsum.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.lendsumapp.lendsum.util.GlobalConstants.LENDSUM_PROFILE_PIC_DIR_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import com.lendsumapp.lendsum.util.ImageUtil.getRotateAngle
import com.lendsumapp.lendsum.util.ImageUtil.rotateBitmap
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltWorker
class UploadImageToDataDirectoryWorker @AssistedInject constructor (
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {

        if (runAttemptCount == 3){
            return Result.failure()
        }

        return suspendCoroutine { continuation ->

            try {
                val fileName = inputData.getString(UPLOAD_PROF_PIC_NAME_KEY)
                val imageUri = inputData.getString(UPLOAD_PROF_PIC_URI_KEY)?.toUri()
                val resolver = applicationContext.contentResolver

                imageUri?.let { uri ->
                    var bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri))

                    val rotationAngle = uri.getRotateAngle(applicationContext)

                    bitmap = bitmap.rotateBitmap(rotationAngle.toFloat())

                    val outputDir = File(applicationContext.filesDir, LENDSUM_PROFILE_PIC_DIR_PATH)

                    if (!outputDir.exists()) {
                        outputDir.mkdirs()
                    } // should succeed

                    val outputFile = File(outputDir, fileName!!)
                    val output = FileOutputStream(outputFile)

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, output)

                    val data: Data = Data.Builder().putString(UPLOAD_PROF_PIC_URI_KEY, outputFile.toUri().toString()).build()

                    continuation.resume(Result.success(data))

                } ?: throw NullPointerException()

            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                continuation.resume(Result.retry())
            }
        }
    }

    companion object{
        private val TAG = UploadImageToDataDirectoryWorker::class.java.simpleName
    }
}
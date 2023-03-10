package com.lendsumapp.lendsum.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.LENDSUM_PROFILE_PIC_DIR_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class UploadImageToDataDirectoryWorker (context: Context, params: WorkerParameters) : Worker(context, params){

    override fun doWork(): Result {
        return try{
            val fileName = inputData.getString(UPLOAD_PROF_PIC_NAME_KEY)
            val imageUri = inputData.getString(UPLOAD_PROF_PIC_URI_KEY)
            val resolver = applicationContext.contentResolver
            val bitmap = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(imageUri)))
            val outputDir = File(applicationContext.filesDir, LENDSUM_PROFILE_PIC_DIR_PATH)

            if (!outputDir.exists()) {
                outputDir.mkdirs()
            } // should succeed

            val outputFile = File(outputDir, fileName!!)
            val output = FileOutputStream(outputFile)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, output)

            val uri: Uri = outputFile.toUri()

            val data: Data = workDataOf(UPLOAD_PROF_PIC_URI_KEY to uri.toString())

            Result.success(data)
        }catch (e: Exception){
            Log.d(TAG, e.toString())
            Result.failure()
        }
    }

    companion object{
        private val TAG = UploadImageToDataDirectoryWorker::class.java.simpleName
    }
}
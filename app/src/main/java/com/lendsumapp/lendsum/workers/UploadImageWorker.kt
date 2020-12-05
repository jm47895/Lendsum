package com.lendsumapp.lendsum.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.lendsumapp.lendsum.repository.EditProfileRepository
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_STORAGE_PROFILE_PIC_PATH
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.UPLOAD_PROF_PIC_URI_KEY
import javax.inject.Inject

class UploadImageWorker(context: Context, params: WorkerParameters) : Worker(context, params){
    override fun doWork(): Result {

        val firebaseStorageReference = Firebase.storage.reference

        val fileName = inputData.getString(UPLOAD_PROF_PIC_NAME_KEY)
        val imageUri = inputData.getString(UPLOAD_PROF_PIC_URI_KEY)

        val profileImageRef = fileName?.let {
            firebaseStorageReference.child(FIREBASE_STORAGE_PROFILE_PIC_PATH).child(it)
            }

        val uploadTask = profileImageRef?.putFile(Uri.parse(imageUri))
        uploadTask?.addOnCompleteListener{ task->
            if(task.isSuccessful){
                Log.d(TAG, "Profile pic uploaded to storage")
            }else{
                Log.d(TAG, "Profile pic failed to upload to storage ${task.exception}")
            }
        }
        return Result.success()
    }

    companion object{
        private val TAG = UploadImageWorker::class.java.simpleName
    }
}
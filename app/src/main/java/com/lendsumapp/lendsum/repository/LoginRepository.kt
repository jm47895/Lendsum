package com.lendsumapp.lendsum.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.lendsumapp.lendsum.auth.EmailAndPassAuthComponent
import com.lendsumapp.lendsum.auth.GoogleAuthComponent
import com.lendsumapp.lendsum.auth.PhoneAuthComponent
import com.lendsumapp.lendsum.data.DataSyncManager
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.data.persistence.LendsumDatabase
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_AUTH_UPDATE_MAP_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_AUTH_UPDATE_MAP_VALUE
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_PROFILE_NAME_KEY
import com.lendsumapp.lendsum.workers.UpdateFirebaseAuthProfileWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val googleAuthComponent: GoogleAuthComponent,
    private val emailAndPassAuthComponent: EmailAndPassAuthComponent,
    private val phoneAuthComponent: PhoneAuthComponent,
    private val dataSyncManager: DataSyncManager,
    private var firebaseAuth: FirebaseAuth,
    private val cacheDb: LendsumDatabase,
    private val workManager: WorkManager
){

    //Firebase
    fun getFirebaseUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun logOut(){
        firebaseAuth.signOut()
    }

    fun deleteFirebaseUser(){
        getFirebaseUser()?.delete()
    }

    //Start of Google Auth functions
    fun handleGoogleSignInIntent(data: Intent): Flow<Response<Unit>>{
        return googleAuthComponent.handleGoogleSignInIntent(data)
    }

    //Start of Email and Pass functions
    suspend fun registerWithEmailAndPassword(email: String, password: String): Response<Unit>{
        return emailAndPassAuthComponent.registerWithEmailAndPassword(email, password)
    }

    fun launchUpdateFirebaseAuthProfileWorker(key: String, value: String) {

        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val updateFirebaseAuthProfile = OneTimeWorkRequestBuilder<UpdateFirebaseAuthProfileWorker>()
            .setConstraints(constraints)
            .setInputData(
                Data.Builder()
                    .putString(key, value)
                    .build()
            )
            .build()

        workManager.enqueue(updateFirebaseAuthProfile)
    }

    suspend fun signInWithEmailAndPass(email: String, password: String): Response<Unit> {

        val response = emailAndPassAuthComponent.signInWithEmailAndPass(email, password)

        if (response.status == Status.SUCCESS) syncAllUserData()

        return response
    }

    suspend fun sendPasswordResetEmail(email: String): Response<Unit>{
        return emailAndPassAuthComponent.sendPasswordResetEmail(email)
    }

    //Phone Auth functions
    suspend fun requestSMSCode(phoneNumber: String, activity: Activity): Response<String>{
        return phoneAuthComponent.requestSMSCode(phoneNumber, activity)
    }

    suspend fun linkPhoneNumWithLoginCredential(credential: PhoneAuthCredential): Response<Unit>{
       return phoneAuthComponent.linkPhoneNumWithLoginCredential(credential)
    }

    //Sync functions
    suspend fun syncAllUserData(): Response<User>{

        val uid = firebaseAuth.currentUser?.uid

        val response = dataSyncManager.syncAllUserDataFromFirestore(uid.toString())

        if(response.status == Status.SUCCESS) response.data?.let { cacheExistingUser(it) }

        return response
    }

    private suspend fun cacheExistingUser(user: User){
        cacheDb.getUserDao().insertUser(user)
    }

    companion object{
        private val TAG = LoginRepository::class.simpleName
    }
}
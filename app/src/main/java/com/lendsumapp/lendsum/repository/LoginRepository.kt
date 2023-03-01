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
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_AUTH_UPDATE_MAP_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.FIREBASE_AUTH_UPDATE_MAP_VALUE
import com.lendsumapp.lendsum.workers.UpdateFirebaseAuthProfileWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val googleAuthComponent: GoogleAuthComponent,
    private val emailAndPassAuthComponent: EmailAndPassAuthComponent,
    private val phoneAuthComponent: PhoneAuthComponent,
    private var firebaseAuth: FirebaseAuth?
){

    //Firebase
    fun getFirebaseUser(): FirebaseUser? {
        return firebaseAuth?.currentUser
    }

    fun logOut(){
        firebaseAuth?.signOut()
    }

    fun deleteFirebaseUser(){
        getFirebaseUser()?.delete()
    }

    //Start of Google Auth functions
    fun handleGoogleSignInIntent(data: Intent): Flow<Response<Unit>>{
        return googleAuthComponent.handleGoogleSignInIntent(data)
    }
    //End of Google Auth functions

    //Start of Email and Pass functions
    fun registerWithEmailAndPassword(email: String, password: String): Flow<Response<Unit>>{
        return emailAndPassAuthComponent.registerWithEmailAndPassword(email, password)
    }

    fun launchUpdateFirebaseAuthProfileWorker(key: String, value: String) {

        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val updateFirebaseAuthProfile = OneTimeWorkRequestBuilder<UpdateFirebaseAuthProfileWorker>()
            .setConstraints(constraints)
            .setInputData(createFirebaseAuthData(key, value))
            .build()

        WorkManager.getInstance().enqueue(updateFirebaseAuthProfile)
    }

    private fun createFirebaseAuthData(key: String, value: String): Data {
        return Data.Builder()
            .putString(FIREBASE_AUTH_UPDATE_MAP_KEY,key)
            .putString(FIREBASE_AUTH_UPDATE_MAP_VALUE, value)
            .build()
    }

    fun signInWithEmailAndPass(email: String, password: String): Flow<Response<Unit>> {
        return emailAndPassAuthComponent.signInWithEmailAndPass(email, password)
    }

    fun sendPasswordResetEmail(email: String): Flow<Response<Unit>>{
        return emailAndPassAuthComponent.sendPasswordResetEmail(email)
    }

    //End of Email and Pass functions

    //Phone Auth functions
    fun requestSMSCode(phoneNumber: String, activity: Activity): Flow<Response<String>>{
        return phoneAuthComponent.requestSMSCode(phoneNumber, activity)
    }

    fun linkPhoneNumWithLoginCredential(credential: PhoneAuthCredential): Flow<Response<Unit>>{
       return phoneAuthComponent.linkPhoneNumWithLoginCredential(credential)
    }
    //End phone auth functions

    companion object{
        private val TAG = LoginRepository::class.simpleName
    }
}
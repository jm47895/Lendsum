package com.lendsumapp.lendsum.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dagger.hilt.android.scopes.ActivityScoped
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PhoneAuthComponent @Inject constructor() {

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val generatedPhoneAuthCode: MutableLiveData<PhoneAuthCredential> = MutableLiveData()
    private val linkPhoneNumWithCredentialStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val testNum = "+19995551234"
    private val code = "123456"

    fun verifyPhoneNumber(phoneNumber: String, activity: Activity){

        val number: String

        if(com.lendsumapp.lendsum.BuildConfig.DEBUG) {
            number = testNum
            firebaseAuth.firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(number, code)
        }else{
            number = phoneNumber
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(number)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "Phone Auth credential retrieved")
            generatedPhoneAuthCode.postValue(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(TAG, "No phone auth credential: $e")
            generatedPhoneAuthCode.postValue(null)
        }
    }

    fun getGeneratedPhoneAuthCode(): MutableLiveData<PhoneAuthCredential>{
        return generatedPhoneAuthCode
    }

    fun linkPhoneNumWithLoginCredential(credential: PhoneAuthCredential){
        firebaseAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task->
            if (task.isSuccessful){
                Log.d(TAG, "Phone number is linked with current credentials")
                linkPhoneNumWithCredentialStatus.postValue(true)
            }else{
                Log.d(TAG, task.exception.toString())
                linkPhoneNumWithCredentialStatus.postValue(false)
            }
        }
    }

    fun getPhoneNumberLinkStatus(): MutableLiveData<Boolean>{
        return linkPhoneNumWithCredentialStatus
    }


    companion object{
        private val TAG = PhoneAuthComponent::class.simpleName
    }
}
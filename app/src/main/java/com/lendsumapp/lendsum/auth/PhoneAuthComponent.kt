package com.lendsumapp.lendsum.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.scopes.ActivityScoped
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ActivityScoped
class PhoneAuthComponent @Inject constructor() {

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val generatedPhoneAuthCode: MutableLiveData<PhoneAuthCredential> = MutableLiveData()
    private val linkPhoneNumWithCredentialStatus: MutableLiveData<Boolean> = MutableLiveData()

    fun verifyPhoneNumber(phoneNumber: String, activity: Activity){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, activity, callbacks)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted, here is the code: ${credential.smsCode}")

            generatedPhoneAuthCode.value = credential

        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.d(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            // Show a message and update the UI
            // ...
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
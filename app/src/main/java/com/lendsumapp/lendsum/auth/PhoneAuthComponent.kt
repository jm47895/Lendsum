package com.lendsumapp.lendsum.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PhoneAuthComponent @Inject constructor() {

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val testNum = "+19995551234"
    private val code = "123456"

    fun requestSMSCode(phoneNumber: String, activity: Activity) = callbackFlow{

        Log.d(TAG, "Phone number: $phoneNumber")

        send(Response(status = Status.LOADING))

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
            .setCallbacks(
                object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        Log.i(TAG, "Phone Auth credential retrieved")
                        trySend(Response(status = Status.SUCCESS))
                        linkPhoneNumWithLoginCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        when(e){
                            is FirebaseAuthInvalidCredentialsException -> trySend(Response(status = Status.ERROR, error = LendsumError.INVALID_PHONE_CREDENTIAL))
                            is FirebaseTooManyRequestsException -> trySend(Response(status = Status.ERROR, error = LendsumError.SMS_LIMIT_MET))
                        }
                        Log.e(TAG, "Phone auth credential failed: $e")
                    }

                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                        Log.i(TAG, "Phone Auth Code Sent")
                        trySend(Response(Status.SUCCESS, data = verificationId))
                    }

                    override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                        Log.i(TAG, "Phone auth code timed out")
                        trySend(Response(Status.ERROR, error = LendsumError.PHONE_CODE_TIMED_OUT))
                    }
                }
            )
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose {

        }
    }
    fun linkPhoneNumWithLoginCredential(credential: PhoneAuthCredential) = callbackFlow<Response<Unit>>{

        send(Response(status = Status.LOADING))

        firebaseAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task->
            if (task.isSuccessful){
                Log.d(TAG, "Phone number is linked with current credentials")
                trySend(Response(status = Status.SUCCESS))
            }else{
                Log.d(TAG, "Phone link failed: ${task.exception}")
                when(task.exception){
                    is FirebaseAuthInvalidCredentialsException -> trySend(Response(status = Status.ERROR, error = LendsumError.INVALID_PHONE_CREDENTIAL))
                }
            }
            channel.close()
        }

        awaitClose{

        }
    }

    companion object{
        private val TAG = PhoneAuthComponent::class.simpleName
    }
}
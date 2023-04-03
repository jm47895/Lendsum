package com.lendsumapp.lendsum.auth

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PhoneAuthComponent @Inject constructor() {

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val testNum = "+19995551234"
    private val code = "123456"

    suspend fun requestSMSCode(phoneNumber: String, activity: Activity): Response<String>{

        return suspendCoroutine { continuation ->
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
                            continuation.resume(Response(status = Status.SUCCESS))
                            //linkPhoneNumWithLoginCredential(credential)
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            when(e){
                                is FirebaseAuthInvalidCredentialsException -> continuation.resume(Response(status = Status.ERROR, error = LendsumError.INVALID_PHONE_CREDENTIAL))
                                is FirebaseTooManyRequestsException -> continuation.resume(Response(status = Status.ERROR, error = LendsumError.SMS_LIMIT_MET))
                            }
                            Log.e(TAG, "Phone auth credential failed: $e")
                        }

                        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                            Log.i(TAG, "Phone Auth Code Sent")
                            continuation.resume(Response(Status.SUCCESS, data = verificationId))
                        }

                        override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                            Log.i(TAG, "Phone auth code timed out")
                            continuation.resume(Response(Status.ERROR, error = LendsumError.PHONE_CODE_TIMED_OUT))
                        }
                    }
                )
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
    suspend fun linkPhoneNumWithLoginCredential(credential: PhoneAuthCredential) : Response<Unit>{
        return suspendCoroutine { continuation ->
            firebaseAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task->
                if (task.isSuccessful){
                    Log.i(TAG, "Phone number is linked with current credentials")
                    continuation.resume(Response(status = Status.SUCCESS))
                }else{
                    Log.e(TAG, "Phone link failed: ${task.exception}")
                    when(task.exception){
                        is FirebaseAuthInvalidCredentialsException -> continuation.resume(Response(status = Status.ERROR, error = LendsumError.INVALID_PHONE_CREDENTIAL))
                    }
                }
            }
        }
    }

    companion object{
        private val TAG = PhoneAuthComponent::class.simpleName
    }
}
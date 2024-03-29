package com.lendsumapp.lendsum.auth

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class EmailAndPassAuthComponent @Inject constructor(
    private val firebaseAuth: FirebaseAuth
){

    suspend fun signInWithEmailAndPass(email: String, password: String): Response<Unit>{
        return suspendCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
                if(task.isSuccessful){
                    continuation.resume(Response(status = Status.SUCCESS))
                    Log.i(TAG, "Sign in with email was successful.")
                }else{
                    continuation.resume(Response(status = Status.ERROR, error = LendsumError.INVALID_LOGIN))
                    Log.e(TAG, "Sign in with email failed" + task.exception)
                }
            }
        }
    }


    suspend fun registerWithEmailAndPassword(email: String, password: String): Response<Unit>{
        return suspendCoroutine { continuation ->
            if(firebaseAuth.currentUser == null) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Log.i(TAG, "Create new user with email and pass was a success")
                        continuation.resume(Response(status = Status.SUCCESS))
                    }else{
                        Log.e(TAG, "Create new user with email and pass failed " + task.exception)
                        val firebaseAuthException = task.exception as FirebaseAuthException
                        continuation.resume(Response(
                            status = Status.ERROR,
                            error = if (firebaseAuthException.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") LendsumError.USER_EMAIL_ALREADY_EXISTS else LendsumError.FAILED_TO_CREATE_USER)
                        )
                    }
                }
            }else{
                val emailAndPassCredential = EmailAuthProvider.getCredential(email, password)
                firebaseAuth.currentUser?.linkWithCredential(emailAndPassCredential)?.addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        continuation.resume(Response(status = Status.SUCCESS))
                        Log.i(TAG, "Email and Google credential link was successful")
                    }else{
                        Log.e(TAG, "Account link was unsuccessful: " + task.exception.toString())
                        if(task.exception.toString().contains("linked")){
                            continuation.resume(Response(status = Status.ERROR, error = LendsumError.LINK_ALREADY_EXISTS))
                        }else{
                            continuation.resume(Response(status = Status.ERROR, error = LendsumError.FAILED_TO_LINK_FIREBASE))
                        }
                    }

                }
            }
        }
    }

    suspend fun updateAuthEmail(email: String): Response<Unit>{

        return suspendCoroutine { continuation ->

            val currentUser = firebaseAuth.currentUser

            currentUser?.updateEmail(email)?.addOnCompleteListener { task->
                if (task.isSuccessful){
                    Log.i(TAG, "User email updated in firebase auth")
                    continuation.resume(Response(status = Status.SUCCESS))
                }else{
                    Log.e(TAG, "User email not update in firebase auth: " + task.exception)
                    when(task.exception){
                        is FirebaseAuthRecentLoginRequiredException -> continuation.resume(Response(status = Status.ERROR, error = LendsumError.LOGIN_REQUIRED))
                        else -> continuation.resume(Response(status = Status.ERROR, error = LendsumError.FAILED_TO_UPDATE_EMAIL))
                    }
                }
            }?: continuation.resume(Response(status = Status.ERROR, error = LendsumError.FAILED_TO_UPDATE_EMAIL))
        }
    }

    suspend fun updateAuthPassword(password: String): Response<Unit>{

        return suspendCoroutine { continuation ->

            val user = firebaseAuth.currentUser

            user?.updatePassword(password)?.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.i(TAG, "Password is updated")
                    continuation.resume(Response(status = Status.SUCCESS))
                }else{
                    Log.e(TAG, "Password failed to update " + task.exception)
                    when(task.exception){
                        is FirebaseAuthRecentLoginRequiredException -> continuation.resume(Response(status = Status.ERROR, error = LendsumError.LOGIN_REQUIRED))
                        else -> continuation.resume(Response(status = Status.ERROR, error = LendsumError.FAILED_TO_UPDATE_PASSWORD))
                    }
                }
            }?: continuation.resume(Response(status = Status.ERROR, error = LendsumError.FAILED_TO_UPDATE_PASSWORD))
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Response<Unit>{

        return suspendCoroutine { continuation ->
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    continuation.resume(Response(Status.SUCCESS))
                    Log.i(TAG, "Reset Password email sent.")
                }else{
                    val exception = task.exception as FirebaseAuthException
                    when(exception.errorCode){
                        "ERROR_USER_NOT_FOUND" -> continuation.resume(Response(status = Status.ERROR, error = LendsumError.USER_NOT_FOUND))
                        else -> continuation.resume(Response(status = Status.ERROR, error = LendsumError.FAILED_TO_SEND))
                    }
                    Log.e(TAG, "Reset Password email failed to send." + task.exception)
                }
            }
        }
    }

    companion object{
        private val TAG = EmailAndPassAuthComponent::class.simpleName
    }

}


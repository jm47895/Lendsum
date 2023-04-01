package com.lendsumapp.lendsum.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseError.ERROR_EMAIL_ALREADY_IN_USE
import com.google.firebase.auth.*
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.util.AndroidUtils
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EmailAndPassAuthComponent @Inject constructor(){

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

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


    fun registerWithEmailAndPassword(email: String, password: String) = callbackFlow<Response<Unit>>{

        send(Response(status = Status.LOADING))

        if(firebaseAuth.currentUser == null) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Log.i(TAG, "Create new user with email and pass was a success")
                    trySend(Response(status = Status.SUCCESS))
                }else{
                    Log.e(TAG, "Create new user with email and pass failed " + task.exception)
                    val firebaseAuthException = task.exception as FirebaseAuthException
                    trySend(Response(
                        status = Status.ERROR,
                        error = if (firebaseAuthException.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") LendsumError.USER_EMAIL_ALREADY_EXISTS else LendsumError.FAILED_TO_CREATE_USER)
                    )
                }
                channel.close()
            }
        }else{
            val emailAndPassCredential = EmailAuthProvider.getCredential(email, password)
            firebaseAuth.currentUser?.linkWithCredential(emailAndPassCredential)?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    trySend(Response(status = Status.SUCCESS))
                    Log.i(TAG, "Email and Google credential link was successful")
                }else{
                    Log.e(TAG, "Account link was unsuccessful: " + task.exception.toString())
                    if(task.exception.toString().contains("linked")){
                        trySend(Response(status = Status.ERROR, error = LendsumError.LINK_ALREADY_EXISTS))
                    }else{
                        trySend(Response(status = Status.ERROR, error = LendsumError.FAILED_TO_LINK_FIREBASE))
                    }
                }
                channel.close()
            }
        }

        awaitClose {

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

    fun updateAuthPassword(password: String){
        val user = firebaseAuth.currentUser

        user?.updatePassword(password)?.addOnCompleteListener { task ->
             if(task.isSuccessful){
                 Log.i(TAG, "Password is updated")
             }else{
                 Log.e(TAG, "Password failed to update " + task.exception)
             }
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


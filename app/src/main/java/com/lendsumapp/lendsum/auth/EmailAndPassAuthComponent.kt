package com.lendsumapp.lendsum.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.*
import com.lendsumapp.lendsum.data.model.Error
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import com.lendsumapp.lendsum.util.AndroidUtils
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class EmailAndPassAuthComponent @Inject constructor(){

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val emailCreateAccountStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val linkWithEmailStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val updateAuthEmailStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val updateAuthPassStatus: MutableLiveData<Boolean> = MutableLiveData()

    fun signInWithEmailAndPass(email: String, password: String) = callbackFlow<Response<Unit>>
    {
        send(Response(status = Status.LOADING))

        when {
            AndroidUtils.isValidEmail(email) && password.isNotEmpty() -> {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        trySend(Response(status = Status.SUCCESS))
                        Log.i(TAG, "Sign in with email was successful.")
                    }else{
                        trySend(Response(status = Status.ERROR, error = Error.INVALID_LOGIN))
                        Log.e(TAG, "Sign in with email failed" + task.exception)
                    }
                    channel.close()
                }
            }
            else -> {
                send(Response(status = Status.ERROR, error = Error.INVALID_LOGIN))
                channel.close()
            }
        }

        awaitClose {

        }
    }


    fun registerWithEmailAndPassword(email: String, password: String){

        if(firebaseAuth.currentUser == null) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Log.d(TAG, "Create new user with email and pass was a success")
                    emailCreateAccountStatus.postValue(true)
                }else{
                    Log.d(TAG, "Create new user with email and pass failed " + task.exception)
                    emailCreateAccountStatus.postValue(false)
                }
            }
        }else{
            val emailAndPassCredential = EmailAuthProvider.getCredential(email, password)
            firebaseAuth.currentUser?.linkWithCredential(emailAndPassCredential)?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    linkWithEmailStatus.postValue(true)
                    Log.d(TAG, "Email and Google or Facebook credential link was successful")
                }else{
                    linkWithEmailStatus.postValue(false)
                    Log.d(TAG, task.exception.toString())
                }
            }
        }
    }

    fun updateAuthEmail(email: String){
        val user = firebaseAuth.currentUser
        user?.updateEmail(email)?.addOnCompleteListener { task->
            if (task.isSuccessful){
                Log.d(TAG, "User email updated in firebase auth")
                updateAuthEmailStatus.postValue(true)
            }else{
                Log.d(TAG, task.exception.toString())
                updateAuthEmailStatus.postValue(false)
            }
        }
    }

    fun getUpdateAuthEmailStatus(): MutableLiveData<Boolean>{
        return updateAuthEmailStatus
    }

    fun updateAuthPassword(password: String){
        val user = firebaseAuth.currentUser

        user?.updatePassword(password)?.addOnCompleteListener { task ->
             if(task.isSuccessful){
                 Log.d(TAG, "Password is updated")
                 updateAuthPassStatus.postValue(true)
             }else{
                 Log.d(TAG, "Password failed to update " + task.exception)
                 updateAuthPassStatus.postValue(false)
             }
        }
    }

    fun getUpdateAuthPassStatus(): MutableLiveData<Boolean>{
        return updateAuthPassStatus
    }

    fun signOutOfEmailAndPass(){
        firebaseAuth.signOut()
    }

    fun getEmailCreateAccountStatus(): MutableLiveData<Boolean>{
        return emailCreateAccountStatus
    }

    fun sendPasswordResetEmail(email: String) = callbackFlow<Response<Unit>>{

        send(Response(status = Status.LOADING))

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                trySend(Response(Status.SUCCESS))
                Log.i(TAG, "Reset Password email sent.")
                channel.close()
            }else{
                trySend(Response(status = Status.ERROR, error = Error.FAILED_TO_SEND))
                Log.e(TAG, "Reset Password email failed to send." + task.exception)
                channel.close(task.exception)
            }
        }

        awaitClose {

        }
    }

    fun getLinkWithCredentialStatus(): MutableLiveData<Boolean> {
        return linkWithEmailStatus
    }

    companion object{
        private val TAG = EmailAndPassAuthComponent::class.simpleName
    }

}


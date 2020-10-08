package com.lendsumapp.lendsum.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class EmailAndPassAuthComponent @Inject constructor(): OnCompleteListener<AuthResult>{

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val emailSignInStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val resetPasswordEmailStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val linkWithEmailStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val updateAuthEmailStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val updateAuthPassStatus: MutableLiveData<Boolean> = MutableLiveData()

    fun signInWithEmailAndPass(email: String, password: String)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this)
    }


    fun registerWithEmailAndPassword(email: String, password: String){

        if(firebaseAuth.currentUser == null) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this)
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

    suspend fun updateAuthEmail(email: String){
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

    suspend fun updateAuthPassword(password: String){
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

    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful){
            emailSignInStatus.postValue(true)
            Log.d(TAG, "register with email : Success.")
        }else{
            emailSignInStatus.postValue(false)
            Log.d(TAG, "register with email : Failure" + task.exception)
        }
    }

    fun signOutOfEmailAndPass(){
        firebaseAuth.signOut()
    }

    fun getEmailSignInStatus(): MutableLiveData<Boolean> {
        return emailSignInStatus
    }

    fun sendPasswordResetEmail(email: String){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Log.d(TAG, "Reset Password email sent.")
                resetPasswordEmailStatus.postValue(true)
            }else{
                Log.d(TAG, "Reset Password email failed to send." + task.exception)
                resetPasswordEmailStatus.postValue(false)
            }
        }
    }

    fun getResetPasswordEmailStatus(): MutableLiveData<Boolean> {
        return resetPasswordEmailStatus
    }

    fun getLinkWithCredentialStatus(): MutableLiveData<Boolean> {
        return linkWithEmailStatus
    }

    companion object{
        private val TAG = EmailAndPassAuthComponent::class.simpleName
    }

}


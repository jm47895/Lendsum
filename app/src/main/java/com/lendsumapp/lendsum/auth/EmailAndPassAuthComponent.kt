package com.lendsumapp.lendsum.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class EmailAndPassAuthComponent @Inject constructor(): OnCompleteListener<AuthResult>{

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val emailSignInStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val resetEmailStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val linkWithEmailStatus: MutableLiveData<Boolean> = MutableLiveData()

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
                resetEmailStatus.postValue(true)
            }else{
                Log.d(TAG, "Reset Password email failed to send.")
                resetEmailStatus.postValue(false)
            }
        }
    }

    fun getResetEmailStatus(): MutableLiveData<Boolean> {
        return resetEmailStatus
    }

    fun getLinkWithCredentialStatus(): MutableLiveData<Boolean> {
        return linkWithEmailStatus
    }

    companion object{
        private val TAG = EmailAndPassAuthComponent::class.simpleName
    }

}


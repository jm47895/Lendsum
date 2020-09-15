package com.lendsumapp.lendsum.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class EmailAndPassAuthComponent @Inject constructor(): OnCompleteListener<AuthResult>{

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val emailSignInStatus: MutableLiveData<Boolean> = MutableLiveData()

    fun signInWithEmailAndPass(email: String, password: String)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this)
    }


    fun registerWithEmailAndPassword(email: String, password: String){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this)
        }

    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful){
            emailSignInStatus.postValue(true)
            Log.d(TAG, "register with email : Success.")
        }else{
            emailSignInStatus.postValue(false)
            Log.d(TAG, "register with email : Failure")
        }
    }

    fun signOutOfEmailAndPass(){
        firebaseAuth.signOut()
        emailSignInStatus.postValue(false)
    }

    fun getEmailSignInStatus(): MutableLiveData<Boolean> {
        return emailSignInStatus
    }

    companion object{
        private val TAG = EmailAndPassAuthComponent::class.simpleName
    }

}


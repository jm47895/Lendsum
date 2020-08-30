package com.lendsumapp.lendsum.auth

import android.app.Activity
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@FragmentScoped
class EmailAndPassAuthComponent @Inject constructor(): OnCompleteListener<AuthResult>{

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    fun signInWithEmailAndPass(email: String, password: String)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password)
    }



    fun registerWithEmailAndPassword(email: String, password: String){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this)
        }

    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful){
            val userEmail = firebaseAuth.currentUser?.email
            Log.d(TAG, "register with email : Success. User email is: {$userEmail}")
        }else{
            Log.d(TAG, "register with email : Failure")
        }
    }

    fun getFirebaseUser(): FirebaseUser?{
        return firebaseAuth.currentUser
    }

    fun signOutOfEmailAndPass(){
        firebaseAuth.signOut()
    }

    fun dismissAuthStateListener(authStateListener: FirebaseAuth.AuthStateListener){
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    companion object{
        private val TAG = EmailAndPassAuthComponent::class.simpleName
    }

}


package com.lendsumapp.lendsum.auth

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.*
import com.lendsumapp.lendsum.util.GlobalConstants.PROFILE_NAME_KEY
import com.lendsumapp.lendsum.util.GlobalConstants.PROFILE_PIC_URI_KEY
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

class EmailAndPassAuthComponent @Inject constructor(){

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val emailSignInStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val emailCreateAccountStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val resetPasswordEmailStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val linkWithEmailStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val updateAuthEmailStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val updateAuthPassStatus: MutableLiveData<Boolean> = MutableLiveData()

    fun signInWithEmailAndPass(email: String, password: String)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task->
            if(task.isSuccessful){
                emailSignInStatus.postValue(true)
                Log.d(TAG, "Sign in with email was successful.")
            }else{
                emailSignInStatus.postValue(false)
                Log.d(TAG, "Sign in with email failed" + task.exception)
            }
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

    fun updateFirebaseAuthProfile(key: String, value: String){
        val currentUser = firebaseAuth.currentUser

        val profileUpdates: UserProfileChangeRequest? = when(key){
            PROFILE_NAME_KEY ->{
                UserProfileChangeRequest.Builder().setDisplayName(value).build()
            }
            PROFILE_PIC_URI_KEY ->{
                UserProfileChangeRequest.Builder().setPhotoUri(value.toUri()).build()
            }
            else-> null
        }


        profileUpdates?.let {
            currentUser?.updateProfile(it)?.addOnCompleteListener { task->
                if(task.isSuccessful){
                    Log.d(TAG, "Firebase auth $key updated")
                }else{
                    Log.d(TAG, "Firebase auth failed to update $key.")
                }
            }
        }
    }

    fun getUpdateAuthPassStatus(): MutableLiveData<Boolean>{
        return updateAuthPassStatus
    }

    fun signOutOfEmailAndPass(){
        firebaseAuth.signOut()
    }

    fun getEmailSignInStatus(): MutableLiveData<Boolean> {
        return emailSignInStatus
    }

    fun getEmailCreateAccountStatus(): MutableLiveData<Boolean>{
        return emailCreateAccountStatus
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


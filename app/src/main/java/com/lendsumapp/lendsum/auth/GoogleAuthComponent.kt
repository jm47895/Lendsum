package com.lendsumapp.lendsum.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import javax.inject.Singleton

@ActivityScoped
class GoogleAuthComponent @Inject constructor(){

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient : GoogleSignInClient
    private val googleAuthState: MutableLiveData<Boolean> = MutableLiveData()

    fun configureGoogleAuth(context: Context, clientId: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

        context.let {
            googleSignInClient = GoogleSignIn.getClient(it, gso)
        }
    }

    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun signOutOfGoogle(){
        googleAuthState.postValue(false)
        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }

    fun getGoogleAuthRequestCode(): Int{
        return RC_SIGN_IN
    }

    fun handleGoogleSignInIntent(requestCode: Int, data: Intent){
        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "FirebaseAuthWithGoogle:" + account.id)
                authenticateCredentials(account.idToken!!)
            }catch (e: ApiException){
                Log.d(TAG, "Firebase Login failed", e)
            }
        }else{
            Log.d(TAG, "Request code not equal")
        }
    }

    private fun authenticateCredentials(idToken: String){

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Google Firebase Sign-in Success")
                    googleAuthState.postValue(true)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "Google Firebase Sign-in Failed", task.exception)
                    googleAuthState.postValue(false)
                }
            }

    }

    fun getGoogleLoginState():MutableLiveData<Boolean>{
        return googleAuthState
    }

    companion object{
        private const val RC_SIGN_IN = 9000
        private val TAG = GoogleAuthComponent::class.simpleName
    }

}

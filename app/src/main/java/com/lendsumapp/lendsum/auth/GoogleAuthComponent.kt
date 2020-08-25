package com.lendsumapp.lendsum.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthComponent @Inject constructor(){

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var user: FirebaseUser

    fun getFirebaseUser(): FirebaseUser?{
        return firebaseAuth.currentUser
    }

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
        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }

    fun sendRequestCode(): Int{
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
        }
    }

    private fun authenticateCredentials(idToken: String){

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    user = firebaseAuth.currentUser!!
                    Log.d(TAG, "Sign in success. Hello: ${user.uid}")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }

    }

    companion object{
        private const val RC_SIGN_IN = 9000
        private val TAG = GoogleAuthComponent::class.simpleName
    }

}

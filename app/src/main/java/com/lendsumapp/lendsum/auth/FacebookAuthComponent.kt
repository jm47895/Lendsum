package com.lendsumapp.lendsum.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FacebookAuthComponent @Inject constructor(){

    private val callbackManager by lazy { CallbackManager.Factory.create() }
    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()

    private fun sendFacebookCredentialsToFirebase(token: AccessToken?, activity: Activity) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token?.token!!)


            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = FirebaseAuth.getInstance().currentUser
                        Log.d(TAG, "signInWithCredential:success: " + user?.email)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)

                    }

                    // ...
                }

    }

    fun getFirebaseUser(): FirebaseUser?{
        return firebaseAuth.currentUser
    }

    fun handleFacebookSignInIntent(requestCode: Int, resultCode: Int, data: Intent){
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun sendFacebookSignInIntent(activity: Activity){
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.d(TAG, "facebook:onSuccess:$loginResult")

                    sendFacebookCredentialsToFirebase(loginResult?.accessToken, activity)
                }

                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "facebook:onError", error)
                }
            })
    }

    fun signOutOfFacebook(){
        LoginManager.getInstance().logOut()
        FirebaseAuth.getInstance().signOut()
    }

    companion object{
        private val TAG = FacebookAuthComponent::class.simpleName
    }
}
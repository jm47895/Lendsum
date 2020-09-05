package com.lendsumapp.lendsum.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import javax.inject.Singleton

@ActivityScoped
class FacebookAuthComponent @Inject constructor(){

    private val callbackManager by lazy { CallbackManager.Factory.create() }
    private val facebookAuthState: MutableLiveData<Boolean> = MutableLiveData()

    private fun sendFacebookCredentialsToFirebase(token: AccessToken?) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token?.token!!)


            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        facebookAuthState.value = true
                        Log.d(TAG, "signInWithCredential:success: ")

                    } else {
                        // If sign in fails, display a message to the user.
                        facebookAuthState.value = false
                        Log.w(TAG, "signInWithCredential:failure", task.exception)

                    }
                }

    }

    fun handleFacebookSignInIntent(requestCode: Int, resultCode: Int, data: Intent){
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun sendFacebookSignInIntent(){
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.d(TAG, "facebook:onSuccess:$loginResult")

                    sendFacebookCredentialsToFirebase(loginResult?.accessToken)
                }

                override fun onCancel() {
                    facebookAuthState.value = false
                    Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    facebookAuthState.value = false
                    Log.d(TAG, "facebook:onError", error)
                }
            })
    }

    fun signOutOfFacebook(){
        facebookAuthState.value = false
        LoginManager.getInstance().logOut()
        FirebaseAuth.getInstance().signOut()
    }

    fun getFacebookAuthState(): MutableLiveData<Boolean>{
        return facebookAuthState
    }

    companion object{
        private val TAG = FacebookAuthComponent::class.simpleName
    }
}
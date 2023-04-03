package com.lendsumapp.lendsum.auth

import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.lendsumapp.lendsum.data.model.LendsumError
import com.lendsumapp.lendsum.data.model.Response
import com.lendsumapp.lendsum.data.model.Status
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GoogleAuthComponent @Inject constructor(){

    suspend fun handleGoogleSignInIntent(intent: Intent) : Response<Unit>{

        return suspendCoroutine { continuation ->

            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)

            if(task.isSuccessful){
                Log.i(TAG, "Get google account success: ${task.result.displayName}")
                val credential = GoogleAuthProvider.getCredential(task.result.idToken, null)

                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { loginTask ->
                    if (loginTask.isSuccessful) {
                        continuation.resume(Response(status = Status.SUCCESS))
                        Log.i(TAG, "Google Firebase Sign-in Success")
                    } else {
                        // If sign in fails, display a message to the user.
                        continuation.resume(Response(status = Status.ERROR, error = LendsumError.FAILED_TO_LINK_FIREBASE))
                        Log.e(TAG, "Google Firebase Sign-in Failed", loginTask.exception)
                    }
                }
            }else{
                continuation.resume(Response(status = Status.ERROR, error = LendsumError.FAILED_TO_GET_GOOGLE_INFO))
                Log.e(TAG, "Get google account failed: ${task.exception}")
            }

        }
    }

    companion object{
        private val TAG = GoogleAuthComponent::class.simpleName
    }

}

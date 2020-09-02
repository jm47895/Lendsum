package com.lendsumapp.lendsum.repository

import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.auth.EmailAndPassAuthComponent
import com.lendsumapp.lendsum.auth.FacebookAuthComponent
import com.lendsumapp.lendsum.auth.GoogleAuthComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import javax.inject.Singleton

@ActivityScoped
class LoginRepository @Inject constructor(
    private val googleAuthComponent: GoogleAuthComponent,
    private val facebookAuthComponent: FacebookAuthComponent,
    private val emailAndPassAuthComponent: EmailAndPassAuthComponent,
    private var firebaseAuth: FirebaseAuth?,
    @ActivityContext private val context: Context
){

    fun getFirebaseUser(): FirebaseUser? {
        return firebaseAuth?.currentUser
    }

    fun configureGoogleAuth(){
        googleAuthComponent.configureGoogleAuth(context, context.resources.getString(R.string.default_web_client_id))
    }

    fun sendGoogleSignInIntent(): Intent{
        return googleAuthComponent.getGoogleSignInIntent()
    }

    fun handleGoogleSignInIntent(resultCode:Int, data: Intent){
        googleAuthComponent.handleGoogleSignInIntent(resultCode, data)
    }

    fun getGoogleRequestCode(): Int{
        return googleAuthComponent.getGoogleAuthRequestCode()
    }

    fun logOutOfGoogle(){
        googleAuthComponent.signOutOfGoogle()
    }

    companion object{
        private val TAG = LoginRepository::class.simpleName
    }
}
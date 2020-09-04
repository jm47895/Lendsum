package com.lendsumapp.lendsum.repository

import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.MutableLiveData
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
    @ActivityContext private var context: Context?
){

    //Firebase
    fun getFirebaseUser(): FirebaseUser? {
        return firebaseAuth?.currentUser
    }

    //Start of Google Auth functions
    fun configureGoogleAuth(){

        context?.let { googleAuthComponent.configureGoogleAuth(it, it.resources.getString(R.string.default_web_client_id)) }
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
    //End of Google Auth functions

    //Start of Email and Pass functions
    fun registerWithEmailAndPassword(email: String, password: String){
        emailAndPassAuthComponent.registerWithEmailAndPassword(email, password)
    }

    fun signInWithEmailAndPass(email: String, password: String){
        emailAndPassAuthComponent.signInWithEmailAndPass(email, password)
    }

    fun initializeAuthStateListener(){
        emailAndPassAuthComponent.initializeAuthStateListener()
    }

    fun dismissAuthStateListener(){
        emailAndPassAuthComponent.dismissAuthStateListener()
        context = null
    }

    fun addFirebaseAuthStateListener(){
        emailAndPassAuthComponent.addFirebaseAuthStateListener()
    }

    fun logOutOfEmailAndPass(){
        emailAndPassAuthComponent.signOutOfEmailAndPass()
    }

    fun getEmailSignInStatus(): MutableLiveData<Boolean> {
        return emailAndPassAuthComponent.getEmailSignInStatus()
    }
    //End of Email and Pass functions

    //Facebook login functions
    fun sendFacebookIntent(){
        facebookAuthComponent.sendFacebookSignInIntent()
    }

    fun handleFacebookSignInIntent(requestCode: Int, resultCode: Int, data: Intent){
        facebookAuthComponent.handleFacebookSignInIntent(requestCode, resultCode, data)
    }

    fun logOutOfFacebook(){
        facebookAuthComponent.signOutOfFacebook()
    }
    //End of Facebook login functions

    companion object{
        private val TAG = LoginRepository::class.simpleName
    }
}
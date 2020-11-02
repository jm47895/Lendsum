package com.lendsumapp.lendsum.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.auth.EmailAndPassAuthComponent
import com.lendsumapp.lendsum.auth.FacebookAuthComponent
import com.lendsumapp.lendsum.auth.GoogleAuthComponent
import com.lendsumapp.lendsum.auth.PhoneAuthComponent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val googleAuthComponent: GoogleAuthComponent,
    private val facebookAuthComponent: FacebookAuthComponent,
    private val emailAndPassAuthComponent: EmailAndPassAuthComponent,
    private val phoneAuthComponent: PhoneAuthComponent,
    private var firebaseAuth: FirebaseAuth?
){

    //Firebase
    fun getFirebaseUser(): FirebaseUser? {
        return firebaseAuth?.currentUser
    }

    fun deleteFirebaseUser(){
        getFirebaseUser()?.delete()
    }

    //Start of Google Auth functions
    fun configureGoogleAuth(context: Context){
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

    fun getGoogleLoginState():MutableLiveData<Boolean>{
        return googleAuthComponent.getGoogleLoginState()
    }
    //End of Google Auth functions

    //Start of Email and Pass functions
    fun registerWithEmailAndPassword(email: String, password: String){
        emailAndPassAuthComponent.registerWithEmailAndPassword(email, password)
    }

    fun updateCreateAccountAuthProfile(key: String, value: String) {
        emailAndPassAuthComponent.updateFirebaseAuthProfile(key, value)
    }

    fun signInWithEmailAndPass(email: String, password: String){
        emailAndPassAuthComponent.signInWithEmailAndPass(email, password)
    }

    fun logOutOfEmailAndPass(){
        emailAndPassAuthComponent.signOutOfEmailAndPass()
    }

    fun getEmailCreateAccountStatus(): MutableLiveData<Boolean>{
        return emailAndPassAuthComponent.getEmailCreateAccountStatus()
    }

    fun getEmailSignInStatus(): MutableLiveData<Boolean> {
        return emailAndPassAuthComponent.getEmailSignInStatus()
    }

    fun sendPasswordResetEmail(email: String){
        emailAndPassAuthComponent.sendPasswordResetEmail(email)
    }

    fun getResetPasswordEmailStatus(): MutableLiveData<Boolean> {
        return emailAndPassAuthComponent.getResetPasswordEmailStatus()
    }

    fun getLinkWithCredentialStatus(): MutableLiveData<Boolean> {
        return emailAndPassAuthComponent.getLinkWithCredentialStatus()
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

    fun getFacebookAuthState(): MutableLiveData<Boolean>{
        return facebookAuthComponent.getFacebookAuthState()
    }
    //End of Facebook login functions

    //Phone Auth functions
    fun sendSMSCode(phoneNumber: String, activity: Activity){
        phoneAuthComponent.verifyPhoneNumber(phoneNumber, activity)
    }

    fun getGeneratedPhoneAuthCode(): MutableLiveData<PhoneAuthCredential>{
        return phoneAuthComponent.getGeneratedPhoneAuthCode()
    }

    fun linkPhoneNumWithLoginCredential(credential: PhoneAuthCredential){
        phoneAuthComponent.linkPhoneNumWithLoginCredential(credential)
    }

    fun getPhoneNumberLinkStatus(): MutableLiveData<Boolean>{
        return phoneAuthComponent.getPhoneNumberLinkStatus()
    }
    //End phone auth functions

    companion object{
        private val TAG = LoginRepository::class.simpleName
    }
}
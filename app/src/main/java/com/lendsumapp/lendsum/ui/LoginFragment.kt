package com.lendsumapp.lendsum.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.auth.EmailAndPassAuthComponent
import com.lendsumapp.lendsum.auth.FacebookAuthComponent
import com.lendsumapp.lendsum.auth.GoogleAuthComponent
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.GlobalConstants.navSignUpType
import com.lendsumapp.lendsum.util.GlobalConstants.returningUser
import com.lendsumapp.lendsum.util.NavSignUpType
import com.lendsumapp.lendsum.util.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment(), View.OnClickListener{

    private val sharedPrefs by lazy { activity?.getPreferences(Context.MODE_PRIVATE) }
    @Inject lateinit var googleAuthComponent: GoogleAuthComponent
    @Inject lateinit var facebookAuthComponent: FacebookAuthComponent
    @Inject lateinit var emailAndPassAuthComponent: EmailAndPassAuthComponent
    @Inject lateinit var networkUtils: NetworkUtils
    @Inject lateinit var androidUtils: AndroidUtils
    private lateinit var emailSignInAuthStateListener: AuthStateListener
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        emailSignInAuthStateListener = AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                findNavController(this).navigate(R.id.action_loginFragment_to_numberVerificationFragment)
            }
        }

        when(sharedPrefs?.getInt(navSignUpType, NavSignUpType.EMAIL_LOGIN.ordinal)){
            NavSignUpType.EMAIL_LOGIN.ordinal ->{
                val emailUser = emailAndPassAuthComponent.getFirebaseUser()
                if(emailUser != null && sharedPrefs?.getBoolean(returningUser, false) == true){
                    findNavController(this).navigate(R.id.action_loginFragment_to_marketplaceFragment)
                }
            }
            NavSignUpType.GOOGLE_LOGIN.ordinal ->{
                val googleUser = googleAuthComponent.getFirebaseUser()
                if(googleUser != null && sharedPrefs?.getBoolean(returningUser, false) == true){
                    findNavController(this).navigate(R.id.action_loginFragment_to_marketplaceFragment)
                }
            }
            NavSignUpType.FACEBOOK_LOGIN.ordinal ->{
                val facebookUser = facebookAuthComponent.getFirebaseUser()
                if(facebookUser != null && sharedPrefs?.getBoolean(returningUser, false) == true){
                    findNavController(this).navigate(R.id.action_loginFragment_to_marketplaceFragment)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(emailSignInAuthStateListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_sign_in_btn.setOnClickListener(this)
        login_sign_up_email_btn.setOnClickListener(this)
        login_forgot_password_tv.setOnClickListener(this)
        login_sign_in_with_facebook.setOnClickListener(this)
        login_sign_in_with_google.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(sharedPrefs?.getInt(navSignUpType, NavSignUpType.EMAIL_LOGIN.ordinal)){
            NavSignUpType.GOOGLE_LOGIN.ordinal ->{
                data?.let { googleAuthComponent.handleGoogleSignInIntent(requestCode, it) }
            }
            NavSignUpType.FACEBOOK_LOGIN.ordinal ->{
                data?.let{ facebookAuthComponent.handleFacebookSignInIntent(requestCode, resultCode, it)}
            }
        }
    }

    override fun onStop() {
        super.onStop()
        emailAndPassAuthComponent.dismissAuthStateListener(emailSignInAuthStateListener)
    }

    private fun sendGoogleSignInIntent(){
        val intent = googleAuthComponent.getGoogleSignInIntent()
        startActivityForResult(intent, googleAuthComponent.sendRequestCode())
    }

    override fun onClick(view: View?) {
        var action: Int = -1
        if(networkUtils.isNetworkAvailable()) {
            when (view?.id) {
                R.id.login_forgot_password_tv -> {
                    action = R.id.action_loginFragment_to_forgotPasswordFragment
                }
                R.id.login_sign_in_btn -> {

                    val signInEmail = login_email_et.text.trim().toString()
                    val signInPass = login_password_et.text.trim().toString()

                    if(!TextUtils.isEmpty(signInEmail) && !TextUtils.isEmpty(signInPass)) {
                        emailAndPassAuthComponent.signInWithEmailAndPass(signInEmail, signInPass)
                    }
                }
                R.id.login_sign_up_email_btn -> {
                    sharedPrefs?.edit()?.putInt(navSignUpType, NavSignUpType.EMAIL_LOGIN.ordinal)?.apply()
                    action = R.id.action_loginFragment_to_createAccountFragment
                }
                R.id.login_sign_in_with_google -> {
                    context?.let {
                        googleAuthComponent.configureGoogleAuth(it, getString(R.string.default_web_client_id))
                        sendGoogleSignInIntent()
                    }
                    sharedPrefs?.edit()?.putInt(navSignUpType, NavSignUpType.GOOGLE_LOGIN.ordinal)?.apply()
                    action = R.id.action_loginFragment_to_numberVerificationFragment

                }
                R.id.login_sign_in_with_facebook -> {
                    sharedPrefs?.edit()?.putInt(navSignUpType, NavSignUpType.FACEBOOK_LOGIN.ordinal)?.apply()
                    LoginManager.getInstance().logInWithReadPermissions(this, listOf("user_photos", "email", "user_birthday", "public_profile"))
                    facebookAuthComponent.sendFacebookSignInIntent()
                    action = R.id.action_loginFragment_to_numberVerificationFragment
                }
            }

            if(action != -1) {
                view?.findNavController()?.navigate(action)
            }
        }else{
            activity?.let { androidUtils.showSnackBar(it, "You are not connected to the internet") }
        }
    }

    companion object{
        private val TAG = LoginFragment::class.simpleName
    }

}
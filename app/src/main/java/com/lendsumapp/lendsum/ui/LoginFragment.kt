package com.lendsumapp.lendsum.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.auth.FacebookAuthComponent
import com.lendsumapp.lendsum.auth.GoogleAuthComponent
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
    @Inject lateinit var utils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(sharedPrefs?.getInt(navSignUpType, NavSignUpType.EMAIL_LOGIN.ordinal)){
            NavSignUpType.EMAIL_LOGIN.ordinal ->{

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
            NavSignUpType.EMAIL_LOGIN.ordinal ->{

            }
            NavSignUpType.GOOGLE_LOGIN.ordinal ->{
                data?.let { googleAuthComponent.handleGoogleSignInIntent(requestCode, it) }
            }
            NavSignUpType.FACEBOOK_LOGIN.ordinal ->{
                data?.let{ facebookAuthComponent.handleFacebookSignInIntent(requestCode, resultCode, it)}
            }
        }
    }

    private fun sendGoogleSignInIntent(){
        val intent = googleAuthComponent.getGoogleSignInIntent()
        startActivityForResult(intent, googleAuthComponent.sendRequestCode())
    }

    override fun onClick(view: View?) {
        var action: Int = -1
        if(utils.isNetworkAvailable()) {
            when (view?.id) {
                R.id.login_forgot_password_tv -> {
                    action = R.id.action_loginFragment_to_forgotPasswordFragment
                }
                R.id.login_sign_in_btn -> {
                    action = R.id.action_loginFragment_to_marketplaceFragment
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
                    login_sign_in_with_facebook.fragment = this
                    login_sign_in_with_facebook.setReadPermissions("email", "public_profile")
                    activity?.let { facebookAuthComponent.sendFacebookSignInIntent(it) }
                    action = R.id.action_loginFragment_to_numberVerificationFragment
                }
            }

            if(action != -1) {
                view?.findNavController()?.navigate(action)
            }
        }else{
            activity?.let {
                val snackBar = Snackbar.make(
                    it.findViewById(android.R.id.content),
                    "There is no internet connection",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Dismiss") {
                }.setActionTextColor(
                    ContextCompat.getColor(
                        it,
                        R.color.colorSecondaryLight
                    )
                )
                    .show()

            }
        }
    }


    companion object{
        private val TAG = LoginFragment::class.simpleName
    }

}
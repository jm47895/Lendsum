package com.lendsumapp.lendsum.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.auth.EmailAndPassAuthComponent
import com.lendsumapp.lendsum.auth.FacebookAuthComponent
import com.lendsumapp.lendsum.databinding.FragmentLoginBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.GlobalConstants.navSignUpType
import com.lendsumapp.lendsum.util.GlobalConstants.returningUser
import com.lendsumapp.lendsum.util.NavSignUpType
import com.lendsumapp.lendsum.util.NetworkUtils
import com.lendsumapp.lendsum.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment(), View.OnClickListener{

    private var _binding: FragmentLoginBinding? = null
    private val binding get() =  _binding
    private val sharedPrefs by lazy { activity?.getPreferences(Context.MODE_PRIVATE) }
    private val loginViewModel: LoginViewModel by viewModels()
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
                val googleUser = loginViewModel.getFirebaseUser()
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.loginSignInBtn?.setOnClickListener(this)
        binding?.loginEmailEt?.setOnClickListener(this)
        binding?.loginForgotPasswordTv?.setOnClickListener(this)
        binding?.loginSignInWithFacebook?.setOnClickListener(this)
        binding?.loginSignInWithGoogle?.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(emailSignInAuthStateListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(sharedPrefs?.getInt(navSignUpType, NavSignUpType.EMAIL_LOGIN.ordinal)){
            NavSignUpType.GOOGLE_LOGIN.ordinal ->{
                data?.let { loginViewModel.handleGoogleSignInIntent(requestCode, it) }
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

    override fun onClick(view: View?) {
        var action: Int = -1
        if(networkUtils.isNetworkAvailable()) {
            when (view?.id) {
                R.id.login_forgot_password_tv -> {
                    action = R.id.action_loginFragment_to_forgotPasswordFragment
                }
                R.id.login_sign_in_btn -> {

                    Log.d(TAG, "Sign In clicked!!!")
                    val signInEmail = binding?.loginEmailEt?.text?.trim().toString()
                    val signInPass = binding?.loginPasswordEt?.text?.trim().toString()

                    if(!TextUtils.isEmpty(signInEmail) && !TextUtils.isEmpty(signInPass)) {
                        emailAndPassAuthComponent.signInWithEmailAndPass(signInEmail, signInPass)
                    }
                }
                R.id.login_sign_up_email_btn -> {
                    Log.d(TAG, "Sign Up clicked!!!")
                    /*sharedPrefs?.edit()?.putInt(navSignUpType, NavSignUpType.EMAIL_LOGIN.ordinal)?.apply()
                    action = R.id.action_loginFragment_to_createAccountFragment*/
                }
                R.id.login_sign_in_with_google -> {
                    context?.let {
                        loginViewModel.configureGoogleAuth()
                        startActivityForResult(loginViewModel.getGoogleAuthIntent(), loginViewModel.getGoogleAuthCode())
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
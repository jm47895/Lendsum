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
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.facebook.login.LoginManager
import com.lendsumapp.lendsum.R
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
    @Inject lateinit var networkUtils: NetworkUtils
    @Inject lateinit var androidUtils: AndroidUtils
    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var emailSignInObserver: Observer<Boolean>
    private lateinit var googleAuthObserver: Observer<Boolean>
    private lateinit var facebookAuthObserver: Observer<Boolean>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(sharedPrefs?.getInt(navSignUpType, NavSignUpType.EMAIL_LOGIN.ordinal)){
            NavSignUpType.EMAIL_LOGIN.ordinal ->{
                val emailUser = loginViewModel.getFirebaseUser()
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
                val facebookUser = loginViewModel.getFirebaseUser()
                if(facebookUser != null && sharedPrefs?.getBoolean(returningUser, false) == true){
                    findNavController(this).navigate(R.id.action_loginFragment_to_marketplaceFragment)
                }
            }
        }

        googleAuthObserver = Observer{ isGoogleLoginSuccessful ->
            if(isGoogleLoginSuccessful){
                sharedPrefs?.edit()?.putInt(navSignUpType, NavSignUpType.GOOGLE_LOGIN.ordinal)?.apply()
                findNavController(this).navigate(R.id.action_loginFragment_to_numberVerificationFragment)
                Log.d(TAG, "Google Auth Observer Success")
            }else{
                Log.d(TAG, "Google Auth Observer Failure")
            }
        }

        facebookAuthObserver = Observer{ isFacebookLoginSuccessful ->
            if (isFacebookLoginSuccessful){
                sharedPrefs?.edit()?.putInt(navSignUpType, NavSignUpType.FACEBOOK_LOGIN.ordinal)?.apply()
                findNavController(this).navigate(R.id.action_loginFragment_to_numberVerificationFragment)
                Log.d(TAG, "Facebook Auth Observer Success")
            }else{
                Log.d(TAG, "Facebook Auth Observer Failure")
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

        binding?.loginSignUpEmailBtn?.setOnClickListener(this)
        binding?.loginSignInBtn?.setOnClickListener(this)
        binding?.loginForgotPasswordTv?.setOnClickListener(this)
        binding?.loginSignInWithFacebook?.setOnClickListener(this)
        binding?.loginSignInWithGoogle?.setOnClickListener(this)

        emailSignInObserver = Observer { isLoginSuccessful ->
            if (isLoginSuccessful){
                Log.d(TAG, "Email login success")
                findNavController(this).navigate(R.id.action_loginFragment_to_numberVerificationFragment)
            }else{
                Log.d(TAG, "Email login failed")
                binding?.loginEmailEt?.error = "The email or password is incorrect"
                binding?.loginPasswordEt?.error = "The email or password is incorrect"
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.let { loginViewModel.handleGoogleSignInIntent(requestCode, it) }

        data?.let{ loginViewModel.handleFacebookSignInIntent(requestCode, resultCode, it)}


    }

    override fun onStop() {
        super.onStop()
    }

    override fun onClick(view: View?) {
        var action: Int = -1
        if(networkUtils.isNetworkAvailable()) {
            when (view?.id) {
                R.id.login_forgot_password_tv -> {
                    action = R.id.action_loginFragment_to_forgotPasswordFragment
                }
                R.id.login_sign_in_btn -> {
                    loginViewModel.getEmailSignInStatus().observe(this, emailSignInObserver)
                    signInEmail = binding?.loginEmailEt?.text?.trim().toString()
                    signInPassword = binding?.loginPasswordEt?.text?.trim().toString()

                    if(!TextUtils.isEmpty(signInEmail) && !TextUtils.isEmpty(signInPassword)) {
                        loginViewModel.signInWithEmailAndPass(signInEmail, signInPassword)
                        sharedPrefs?.edit()?.putInt(navSignUpType, NavSignUpType.EMAIL_LOGIN.ordinal)?.apply()
                    }else{
                        binding?.loginEmailEt?.error = "The email or password is incorrect"
                        binding?.loginPasswordEt?.error = "The email or password is incorrect"
                    }
                }
                R.id.login_sign_up_email_btn -> {
                    sharedPrefs?.edit()?.putInt(navSignUpType, NavSignUpType.EMAIL_LOGIN.ordinal)?.apply()
                    action = R.id.action_loginFragment_to_createAccountFragment
                }
                R.id.login_sign_in_with_google -> {
                    loginViewModel.getGoogleLoginState().observe(this, googleAuthObserver)
                    loginViewModel.configureGoogleAuth()
                    startActivityForResult(loginViewModel.getGoogleAuthIntent(), loginViewModel.getGoogleAuthCode())
                }
                R.id.login_sign_in_with_facebook -> {
                    loginViewModel.getFacebookAuthState().observe(this, facebookAuthObserver)
                    LoginManager.getInstance().logInWithReadPermissions(this, listOf("user_photos", "email", "user_birthday", "public_profile"))
                    loginViewModel.sendFacebookIntent()
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
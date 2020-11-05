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
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.databinding.FragmentLoginBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.GlobalConstants.NAV_SIGN_UP_TYPE
import com.lendsumapp.lendsum.util.GlobalConstants.RETURNING_USER
import com.lendsumapp.lendsum.util.NavSignUpType
import com.lendsumapp.lendsum.util.NetworkUtils
import com.lendsumapp.lendsum.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment(), View.OnClickListener{

    private var _binding: FragmentLoginBinding? = null
    private val binding get() =  _binding!!
    private val sharedPrefs by lazy { activity?.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE) }
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var emailSignInObserver: Observer<Boolean>
    private lateinit var googleAuthObserver: Observer<Boolean>
    private lateinit var facebookAuthObserver: Observer<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseUser = loginViewModel.getFirebaseUser()

        if(firebaseUser != null
            && sharedPrefs?.getBoolean(RETURNING_USER, false) == true){
            findNavController().navigate(R.id.action_loginFragment_to_marketplaceFragment)
        }

        googleAuthObserver = Observer{ isGoogleLoginSuccessful ->
            if(isGoogleLoginSuccessful){
                sharedPrefs?.edit()?.putInt(NAV_SIGN_UP_TYPE, NavSignUpType.GOOGLE_LOGIN.ordinal)?.apply()
                findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
                Log.d(TAG, "Google Auth Observer Success")
            }else{
                Log.d(TAG, "Google Auth Observer Failure")
            }
        }

        facebookAuthObserver = Observer{ isFacebookLoginSuccessful ->
            if (isFacebookLoginSuccessful){
                sharedPrefs?.edit()?.putInt(NAV_SIGN_UP_TYPE, NavSignUpType.FACEBOOK_LOGIN.ordinal)?.apply()
                findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginSignUpEmailBtn.setOnClickListener(this)
        binding.loginSignInBtn.setOnClickListener(this)
        binding.loginForgotPasswordTv.setOnClickListener(this)
        binding.loginSignInWithFacebook.setOnClickListener(this)
        binding.loginSignInWithGoogle.setOnClickListener(this)

        emailSignInObserver = Observer { isLoginSuccessful ->
            if (isLoginSuccessful){
                Log.d(TAG, "Email login success")
                sharedPrefs?.edit()?.putBoolean(RETURNING_USER, true)?.apply()
                findNavController().navigate(R.id.action_loginFragment_to_numberVerificationFragment)
            }else{
                Log.d(TAG, "Email login failed")
                binding.loginEmailEt.error = getString(R.string.email_or_pass_wrong)
                binding.loginPasswordEt.error = getString(R.string.email_or_pass_wrong)
            }
        }
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

    override fun onClick(view: View?) {

        var action: Int = -1
        val isOnline = context?.let { NetworkUtils.isNetworkAvailable(it) }

        if(isOnline!!) {
            when (view?.id) {
                R.id.login_forgot_password_tv -> {
                    action = R.id.action_loginFragment_to_forgotPasswordFragment
                }
                R.id.login_sign_in_btn -> {
                    loginViewModel.getEmailSignInStatus().observe(viewLifecycleOwner, emailSignInObserver)
                    val signInEmail = binding.loginEmailEt.text?.trim().toString()
                    val signInPassword = binding.loginPasswordEt.text?.trim().toString()

                    if (!TextUtils.isEmpty(signInEmail) && !TextUtils.isEmpty(signInPassword)) {
                        loginViewModel.signInWithEmailAndPass(signInEmail, signInPassword)
                        sharedPrefs?.edit()
                            ?.putInt(NAV_SIGN_UP_TYPE, NavSignUpType.EMAIL_LOGIN.ordinal)?.apply()
                    } else {
                        binding.loginEmailEt.error = getString(R.string.email_or_pass_wrong)
                        binding.loginPasswordEt.error = getString(R.string.email_or_pass_wrong)
                    }
                }
                R.id.login_sign_up_email_btn -> {
                    sharedPrefs?.edit()?.putInt(NAV_SIGN_UP_TYPE, NavSignUpType.EMAIL_LOGIN.ordinal)
                        ?.apply()
                    action = R.id.action_loginFragment_to_createAccountFragment
                }
                R.id.login_sign_in_with_google -> {
                    loginViewModel.getGoogleLoginState().observe(viewLifecycleOwner, googleAuthObserver)
                    loginViewModel.configureGoogleAuth()
                    startActivityForResult(
                        loginViewModel.getGoogleAuthIntent(),
                        loginViewModel.getGoogleAuthCode()
                    )
                }
                R.id.login_sign_in_with_facebook -> {
                    loginViewModel.getFacebookAuthState().observe(viewLifecycleOwner, facebookAuthObserver)
                    LoginManager.getInstance().logInWithReadPermissions(
                        this,
                        listOf("user_photos", "email", "user_birthday", "public_profile")
                    )
                    loginViewModel.sendFacebookIntent()
                }
            }

            if (action != -1) {
                findNavController().navigate(action)
            }

        }else{
            activity?.let { AndroidUtils.showSnackBar(it, getString(R.string.not_connected_internet)) }
        }
    }

    companion object{
        private val TAG = LoginFragment::class.simpleName
    }

}
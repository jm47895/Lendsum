package com.lendsumapp.lendsum.ui

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.databinding.FragmentCreateAccountBinding
import com.lendsumapp.lendsum.databinding.FragmentLoginBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.NAV_SIGN_UP_TYPE
import com.lendsumapp.lendsum.util.GlobalConstants.RETURNING_USER
import com.lendsumapp.lendsum.util.NavSignUpType
import com.lendsumapp.lendsum.viewmodel.CreateAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_account.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
class CreateAccountFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() =  _binding
    private val sharedPrefs by lazy { activity?.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE) }
    private val createAccountViewModel: CreateAccountViewModel by viewModels()
    private lateinit var emailSignUpObserver: Observer<Boolean>
    private lateinit var linkWithEmailObserver: Observer<Boolean>
    @Inject lateinit var androidUtils: AndroidUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        emailSignUpObserver = Observer { isSignUpSuccessful ->

            if (isSignUpSuccessful){
                sharedPrefs?.edit()?.putInt(NAV_SIGN_UP_TYPE, NavSignUpType.EMAIL_LOGIN.ordinal)?.apply()
                Log.d(TAG, "Email sign up success")
                findNavController(this).navigate(R.id.action_createAccountFragment_to_numberVerificationFragment)
            }else{
                Log.d(TAG, "Email sign up failure")
            }

        }

        linkWithEmailObserver = Observer { isLinkWithEmailSuccessful->
            if(isLinkWithEmailSuccessful){
                Log.d(TAG, "Link email with other credential provider success")
                findNavController(this).navigate(R.id.action_createAccountFragment_to_numberVerificationFragment)
            }else{
                Log.d(TAG, "Link email with other credential provider failed")
                sharedPrefs?.edit()?.putBoolean(RETURNING_USER, true)?.apply()
                activity?.let { androidUtils.showSnackBar(it, getString(R.string.account_already_exists)) }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this){
            sharedPrefs?.getBoolean(RETURNING_USER, false)?.let { logoutOrDeleteUserHandler(it) }
            findNavController(this@CreateAccountFragment).navigate(R.id.action_createAccountFragment_to_loginFragment)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = createAccountViewModel.getFirebaseUser()

        if(user != null){
            binding?.createUserFirstNameEt?.setText(user.displayName?.let { getFirstName(it) })
            binding?.createUserLastNameEt?.setText(user.displayName?.let { getLastName(it) })
            binding?.createUserEmailEt?.setText(user.email)
        }

        binding?.createAccountBackBtn?.setOnClickListener(this)
        binding?.createAccountNextBtn?.setOnClickListener(this)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signUpUser(firstName: String, lastName: String, email: String, password: String) {
        createAccountViewModel.createUserAccount(email, password)
    }

    private fun isValidAccountForm(firstName: String, lastName: String, email: String, password: String, matchPassword: String): Boolean {

        if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)) {
            binding?.createUserFirstNameEt?.error = getString(R.string.first_name_error_msg)
            binding?.createUserLastNameEt?.error = getString(R.string.last_name_err_msg)
            return false
        } else if (!androidUtils.isValidEmail(email)) {
            binding?.createUserEmailEt?.error = getString(R.string.invalid_email_err_msg)
            return false
        } else if (TextUtils.isEmpty(password) || !androidUtils.isValidPassword(password)
        ) {
            binding?.createUserPasswordEt?.error = getString(R.string.password_param_err_msg)
            return false
        }else if(password != matchPassword){
            binding?.createUserMatchPasswordEt?.error = getString(R.string.pass_dont_match_err_msg)
            binding?.createUserPasswordEt?.error = getString(R.string.pass_dont_match_err_msg)
            return false
        }
        return true
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.create_account_back_btn->{

                sharedPrefs?.getBoolean(RETURNING_USER, false)?.let { logoutOrDeleteUserHandler(it) }

                view.findNavController().navigate(R.id.action_createAccountFragment_to_loginFragment)
            }
            R.id.create_account_next_btn->{

                context?.let { androidUtils.hideKeyboard(it, view) }

                val firstName = binding?.createUserFirstNameEt?.text.toString().trim()
                val lastName = binding?.createUserLastNameEt?.text.toString().trim()
                val email = binding?.createUserEmailEt?.text.toString().trim()
                val password = binding?.createUserPasswordEt?.text.toString().trim()
                val matchPassword = binding?.createUserMatchPasswordEt?.text.toString().trim()

                if(isValidAccountForm(firstName, lastName, email, password, matchPassword)){
                    createAccountViewModel.getEmailSignUpStatus().observe(viewLifecycleOwner, emailSignUpObserver)
                    createAccountViewModel.getLinkWithCredentialStatus().observe(viewLifecycleOwner, linkWithEmailObserver)
                    signUpUser(firstName, lastName, email, password)
                }
            }
        }
    }

    private fun getFirstName(name: String): String{
        return name.substring(0, name.indexOf(" "))
    }

    private fun getLastName(name: String): String{
        return name.substring(name.indexOf(" ") + 1)
    }

    private fun logoutOrDeleteUserHandler(isReturningUser : Boolean){
        if(isReturningUser){
            when (sharedPrefs?.getInt(NAV_SIGN_UP_TYPE, NavSignUpType.EMAIL_LOGIN.ordinal)) {
                NavSignUpType.EMAIL_LOGIN.ordinal -> {
                    createAccountViewModel.logOutOfEmailAndPass()
                }
                NavSignUpType.GOOGLE_LOGIN.ordinal -> {
                    createAccountViewModel.configureGoogleAuth()
                    createAccountViewModel.logOutOfGoogle()
                }
                NavSignUpType.FACEBOOK_LOGIN.ordinal -> {
                    createAccountViewModel.logOutOfFacebook()
                }
            }
        }else {
            createAccountViewModel.deleteFirebaseUser()
        }
    }

    companion object{
        private val TAG = CreateAccountFragment::class.simpleName
    }
}
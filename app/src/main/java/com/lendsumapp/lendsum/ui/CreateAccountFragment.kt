package com.lendsumapp.lendsum.ui

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.NAV_SIGN_UP_TYPE
import com.lendsumapp.lendsum.util.NavSignUpType
import com.lendsumapp.lendsum.viewmodel.CreateAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_account.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
class CreateAccountFragment : Fragment() {

    private val sharedPrefs by lazy { activity?.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE) }
    private val createAccountViewModel: CreateAccountViewModel by viewModels()
    private lateinit var emailSignUpObserver: Observer<Boolean>

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        create_account_next_btn.setOnClickListener {

            val firstName = create_user_first_name_et.text.toString().trim()
            val lastName = create_user_last_name_et.text.toString().trim()
            val email = create_user_email_et.text.toString().trim()
            val password = create_user_password_et.text.toString().trim()
            val matchPassword = create_user_match_password_et.text.toString().trim()

            if(isValidAccountForm(firstName, lastName, email, password, matchPassword)){
                createAccountViewModel.getEmailSignUpStatus().observe(viewLifecycleOwner, emailSignUpObserver)
                signUpUser(firstName, lastName, email, password)
            }

        }

        create_account_back_btn.setOnClickListener {
            createAccountViewModel.logOutOfEmailAndPass()
            view.findNavController().navigate(R.id.action_createAccountFragment_to_loginFragment)
        }
    }

    private fun signUpUser(firstName: String, lastName: String, email: String, password: String) {
        createAccountViewModel.createUserAccount(email, password)
    }

    private fun isValidAccountForm(firstName: String, lastName: String, email: String, password: String, matchPassword: String): Boolean {

        if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)) {
            create_user_first_name_et.error = getString(R.string.first_name_error_msg)
            create_user_last_name_et.error = getString(R.string.last_name_err_msg)
            return false
        } else if (!isValidEmail(email)) {
            create_user_email_et.error = getString(R.string.invalid_email_err_msg)
            return false
        } else if (TextUtils.isEmpty(password) || !isValidPassword(password)
        ) {
            create_user_password_et.error = getString(R.string.password_param_err_msg)
            return false
        }else if(password != matchPassword){
            create_user_match_password_et.error = getString(R.string.pass_dont_match_err_msg)
            create_user_password_et.error = getString(R.string.pass_dont_match_err_msg)
            return false
        }
        return true
    }

    //Password validation with regex for at least one letter, one number, and one number in password
    private fun isValidPassword(password: String): Boolean {
        val matchCase: Matcher
        val isValid: Boolean
        val pattern: Pattern = Pattern.compile(PASSWORD_PATTERN)
        matchCase = pattern.matcher(password)
        isValid = matchCase.matches()
        return isValid
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }


    companion object{
        private val TAG = CreateAccountFragment::class.simpleName
        private const val PASSWORD_PATTERN = "^((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@%+/\'!#$^?:,(){}~_.]).{6,20})$"
    }
}
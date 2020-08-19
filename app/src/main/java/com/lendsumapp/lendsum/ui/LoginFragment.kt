package com.lendsumapp.lendsum.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.util.GlobalConstants.backNavSignUpType
import com.lendsumapp.lendsum.util.NavSignUp
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    private val sharedPrefs by lazy { activity?.getPreferences(Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        login_sign_in_btn.setOnClickListener {

            view.findNavController().navigate(R.id.action_loginFragment_to_marketplaceFragment)
        }

        login_sign_up_email_btn.setOnClickListener {

            sharedPrefs?.edit()?.putInt(backNavSignUpType, NavSignUp.SLOW_LOGIN.ordinal)?.apply()

            view.findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
        }

        login_forgot_password_tv.setOnClickListener {
            view.findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        login_sign_in_with_facebook.setOnClickListener {

            sharedPrefs?.edit()?.putInt(backNavSignUpType, NavSignUp.FAST_LOGIN.ordinal)?.apply()

            view.findNavController().navigate(R.id.action_loginFragment_to_numberVerificationFragment)
        }

        login_sign_in_with_google.setOnClickListener {

            sharedPrefs?.edit()?.putInt(backNavSignUpType, NavSignUp.FAST_LOGIN.ordinal)?.apply()

            view.findNavController().navigate(R.id.action_loginFragment_to_numberVerificationFragment)
        }
    }


}
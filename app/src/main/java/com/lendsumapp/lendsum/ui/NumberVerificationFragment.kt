package com.lendsumapp.lendsum.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.util.GlobalConstants.backNavSignUpType
import com.lendsumapp.lendsum.util.NavSignUp
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_number_verification.*


class NumberVerificationFragment : Fragment() {

    private val sharedPrefs by lazy { activity?.getPreferences(Context.MODE_PRIVATE) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_number_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sharedPrefs?.getInt(backNavSignUpType, 0) == NavSignUp.FAST_LOGIN.ordinal){
            number_verifiction_back_btn.setOnClickListener {
                view.findNavController().navigate(R.id.action_numberVerificationFragment_to_loginFragment)
            }
        }else{
            number_verifiction_back_btn.setOnClickListener {
                view.findNavController().navigate(R.id.action_numberVerificationFragment_to_createAccountFragment)
            }
        }


        number_verifiction_next_btn.setOnClickListener {
            view.findNavController().navigate(R.id.action_numberVerificationFragment_to_termsConditionsFragment)
        }

    }
}
package com.lendsumapp.lendsum.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.auth.FacebookAuthComponent
import com.lendsumapp.lendsum.auth.GoogleAuthComponent
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.navSignUpType
import com.lendsumapp.lendsum.util.GlobalConstants.returningUser
import com.lendsumapp.lendsum.util.NavSignUpType
import com.lendsumapp.lendsum.viewmodel.NumberVerificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_number_verification.*
import javax.inject.Inject

@AndroidEntryPoint
class NumberVerificationFragment : Fragment() {

    private val sharedPrefs by lazy { activity?.getPreferences(Context.MODE_PRIVATE) }
    private val numberVerificationViewModel: NumberVerificationViewModel by viewModels()
    @Inject lateinit var googleAuth : GoogleAuthComponent
    @Inject lateinit var facebookAuthComponent: FacebookAuthComponent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_number_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sharedPrefs?.getInt(navSignUpType, NavSignUpType.EMAIL_LOGIN.ordinal) == NavSignUpType.EMAIL_LOGIN.ordinal){
            number_verifiction_back_btn.setOnClickListener {
                view.findNavController().navigate(R.id.action_numberVerificationFragment_to_createAccountFragment)

            }
        }else{
            number_verifiction_back_btn.setOnClickListener {
                when(sharedPrefs?.getInt(navSignUpType, NavSignUpType.EMAIL_LOGIN.ordinal)){
                    NavSignUpType.EMAIL_LOGIN.ordinal ->{

                    }
                    NavSignUpType.GOOGLE_LOGIN.ordinal ->{
                        numberVerificationViewModel.configureGoogleAuth()
                        numberVerificationViewModel.logOutOfGoogle()
                    }
                    NavSignUpType.FACEBOOK_LOGIN.ordinal ->{
                        facebookAuthComponent.signOutOfFacebook()
                    }
                }

                view.findNavController().navigate(R.id.action_numberVerificationFragment_to_loginFragment)
            }
        }

        if(sharedPrefs?.getBoolean(returningUser, false) == false){
            number_verifiction_next_btn.setOnClickListener {
                view.findNavController().navigate(R.id.action_numberVerificationFragment_to_termsConditionsFragment)
            }
        }else{
            number_verifiction_next_btn.setOnClickListener {
                view.findNavController().navigate(R.id.action_numberVerificationFragment_to_marketplaceFragment)
            }
        }
    }
}
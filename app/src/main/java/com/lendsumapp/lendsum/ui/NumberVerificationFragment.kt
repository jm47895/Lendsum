package com.lendsumapp.lendsum.ui

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.hbb20.CountryCodePicker
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.databinding.FragmentNumberVerificationBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.GlobalConstants.NAV_SIGN_UP_TYPE
import com.lendsumapp.lendsum.util.GlobalConstants.RETURNING_USER
import com.lendsumapp.lendsum.util.NavSignUpType
import com.lendsumapp.lendsum.util.NetworkUtils
import com.lendsumapp.lendsum.viewmodel.NumberVerificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

@AndroidEntryPoint
class NumberVerificationFragment : Fragment(), View.OnClickListener, CountryCodePicker.PhoneNumberValidityChangeListener{

    private var _binding: FragmentNumberVerificationBinding? = null
    private val binding get() = _binding
    private val sharedPrefs by lazy { activity?.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE) }
    private val numberVerificationViewModel: NumberVerificationViewModel by viewModels()
    private lateinit var phoneNumberVerificationObserver: Observer<PhoneAuthCredential>
    private var credential: PhoneAuthCredential? = null
    private var isPhoneNumberValid = false
    @Inject lateinit var androidUtils: AndroidUtils
    @Inject lateinit var networkUtils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phoneNumberVerificationObserver = Observer { phoneAuthCredential ->
            credential = phoneAuthCredential
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNumberVerificationBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.countryCodeSp?.setPhoneNumberValidityChangeListener(this)
        binding?.countryCodeSp?.registerCarrierNumberEditText(binding?.numberVerificationPhoneEt)
        binding?.numberVerificationSendCodeBtn?.setOnClickListener(this)
        binding?.numberVerificationVerifyCodeBtn?.setOnClickListener(this)
        binding?.numberVerificationBackBtn?.setOnClickListener(this)
        binding?.numberVerificationNextBtn?.setOnClickListener(this)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        val isOnline = context?.let { networkUtils.isNetworkAvailable(it) }

        if(isOnline!!) {
            when (view?.id) {
                R.id.number_verification_send_code_btn -> {

                    context?.let { androidUtils.hideKeyboard(it, view) }

                    if(isPhoneNumberValid){

                        activity?.let { androidUtils.showSnackBar(it, getString(R.string.verification_code_sent)) }

                        val phoneNumber = binding?.countryCodeSp?.fullNumberWithPlus

                        Log.d(TAG, "Phone number $phoneNumber sending")
                        activity?.let { mainActivity ->
                            phoneNumber?.let { number -> numberVerificationViewModel.sendSMSCode(number, mainActivity) }
                        }

                        numberVerificationViewModel.getGeneratedPhoneAuthCode().observe(this, phoneNumberVerificationObserver)

                    }else{
                        Log.d(TAG, "Phone number not sending")
                        binding?.numberVerificationPhoneEt?.error = getString(R.string.phone_number_invalid)
                    }
                }
                R.id.number_verification_verify_code_btn -> {

                    context?.let { androidUtils.hideKeyboard(it, view) }

                    val code = binding?.numberVerificationCodeEt?.text?.trim().toString()
                    if (code == credential?.smsCode) {

                        Log.d(TAG, "Code: $code matches $credential")

                        numberVerificationViewModel.linkPhoneNumWithLoginCredential(credential!!)

                        binding?.numberVerificationNextBtn?.visibility = View.VISIBLE

                    } else {
                        Log.d(TAG, "Code: $code does not match $credential")

                        activity?.let { androidUtils.showSnackBar(it, getString(R.string.code_not_match)) }
                    }
                }
                R.id.number_verification_next_btn -> {
                    if(sharedPrefs?.getBoolean(RETURNING_USER, false) == false){
                        view.findNavController().navigate(R.id.action_numberVerificationFragment_to_termsConditionsFragment)
                    }else{
                        view.findNavController().navigate(R.id.action_numberVerificationFragment_to_marketplaceFragment)
                    }
                }
                R.id.number_verification_back_btn -> {
                    if (sharedPrefs?.getInt(NAV_SIGN_UP_TYPE, NavSignUpType.EMAIL_LOGIN.ordinal) == NavSignUpType.EMAIL_LOGIN.ordinal) {
                        view.findNavController()
                            .navigate(R.id.action_numberVerificationFragment_to_createAccountFragment)
                    } else {
                        when (sharedPrefs?.getInt(NAV_SIGN_UP_TYPE, NavSignUpType.EMAIL_LOGIN.ordinal)) {
                            NavSignUpType.GOOGLE_LOGIN.ordinal -> {
                                numberVerificationViewModel.configureGoogleAuth()
                                numberVerificationViewModel.logOutOfGoogle()
                            }
                            NavSignUpType.FACEBOOK_LOGIN.ordinal -> {
                                numberVerificationViewModel.logOutOfFacebook()
                            }
                        }

                        view.findNavController()
                            .navigate(R.id.action_numberVerificationFragment_to_loginFragment)
                    }
                }
            }
        }else{
            activity?.let { androidUtils.showSnackBar(it, getString(R.string.not_connected_internet)) }
        }
    }

    companion object{
        private val TAG = NumberVerificationFragment::class.simpleName
    }

    override fun onValidityChanged(isValidNumber: Boolean) {
        isPhoneNumberValid = if(isValidNumber){
            Log.d(TAG, "Phone Number: " + binding?.countryCodeSp?.fullNumberWithPlus)
            true
        }else{
            false
        }
    }
}
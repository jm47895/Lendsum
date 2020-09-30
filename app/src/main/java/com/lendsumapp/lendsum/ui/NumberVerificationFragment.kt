package com.lendsumapp.lendsum.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.PhoneAuthCredential
import com.hbb20.CountryCodePicker
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.databinding.FragmentNumberVerificationBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.GlobalConstants.NUMBER_VERIFIED
import com.lendsumapp.lendsum.util.GlobalConstants.RETURNING_USER
import com.lendsumapp.lendsum.util.NetworkUtils
import com.lendsumapp.lendsum.viewmodel.NumberVerificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NumberVerificationFragment : Fragment(), View.OnClickListener, CountryCodePicker.PhoneNumberValidityChangeListener{

    private var _binding: FragmentNumberVerificationBinding? = null
    private val binding get() = _binding
    private val sharedPrefs by lazy { activity?.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE) }
    private val numberVerificationViewModel: NumberVerificationViewModel by viewModels()
    private lateinit var phoneNumberCredentialObserver: Observer<PhoneAuthCredential>
    private lateinit var linkPhoneNumberStatusObserver: Observer<Boolean>
    private var credential: PhoneAuthCredential? = null
    private var isPhoneNumberValid = false
    @Inject lateinit var androidUtils: AndroidUtils
    @Inject lateinit var networkUtils: NetworkUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phoneNumberCredentialObserver = Observer { phoneAuthCredential ->
            credential = phoneAuthCredential
        }

        linkPhoneNumberStatusObserver = Observer { isPhoneNumberLinked ->
            if(isPhoneNumberLinked){

                sharedPrefs?.edit()?.putBoolean(NUMBER_VERIFIED, true)?.apply()

                if(sharedPrefs?.getBoolean(RETURNING_USER, false) == false){
                    cacheUserCredentials()
                    findNavController().navigate(R.id.action_numberVerificationFragment_to_termsConditionsFragment)
                }else{
                    findNavController().navigate(R.id.action_numberVerificationFragment_to_marketplaceFragment)
                }

            }else{
                //This hits when a user already has an account linked
                activity?.let { androidUtils.showSnackBar(it, "There seems to be a problem linking your phone number to your account") }
            }
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

    override fun onStop() {
        super.onStop()

        numberVerificationViewModel.getPhoneNumberLinkStatus().removeObserver(linkPhoneNumberStatusObserver)
        numberVerificationViewModel.getGeneratedPhoneAuthCode().removeObserver(phoneNumberCredentialObserver)

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

                        numberVerificationViewModel.getGeneratedPhoneAuthCode().observe(viewLifecycleOwner, phoneNumberCredentialObserver)

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

                        if(sharedPrefs?.getBoolean(RETURNING_USER, false) == false){
                            numberVerificationViewModel.getPhoneNumberLinkStatus().observe(viewLifecycleOwner, linkPhoneNumberStatusObserver)
                            numberVerificationViewModel.linkPhoneNumWithLoginCredential(credential!!)
                        }else{
                            findNavController().navigate(R.id.action_numberVerificationFragment_to_marketplaceFragment)
                        }

                    } else {

                        Log.d(TAG, "Code: $code does not match $credential")

                        activity?.let { androidUtils.showSnackBar(it, getString(R.string.code_not_match)) }
                    }
                }
                R.id.number_verification_back_btn -> {
                    if (sharedPrefs?.getBoolean(RETURNING_USER, false) == false) {
                        view.findNavController().navigate(R.id.action_numberVerificationFragment_to_createAccountFragment)
                    } else {
                        view.findNavController().navigate(R.id.action_numberVerificationFragment_to_loginFragment)
                    }
                }
            }
        }else{
            activity?.let { androidUtils.showSnackBar(it, getString(R.string.not_connected_internet)) }
        }
    }

    private fun cacheUserCredentials(){
        numberVerificationViewModel.insertUserIntoSqlCache()
    }

    override fun onValidityChanged(isValidNumber: Boolean) {
        isPhoneNumberValid = if(isValidNumber){
            Log.d(TAG, "Phone Number: " + binding?.countryCodeSp?.fullNumberWithPlus)
            true
        }else{
            false
        }
    }

    companion object{
        private val TAG = NumberVerificationFragment::class.simpleName
    }

}
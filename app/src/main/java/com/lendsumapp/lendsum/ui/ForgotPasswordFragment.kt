package com.lendsumapp.lendsum.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.databinding.FragmentForgotPasswordBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() =  _binding!!
    //private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()
    private lateinit var resetEmailStatusObserver: Observer<Boolean>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.forgotSendResetPassBtn.setOnClickListener(this)

        resetEmailStatusObserver = Observer { isEmailSent ->
            if(isEmailSent){
                activity?.let { AndroidUtils.showSnackBar(it, getString(R.string.reset_email_sent)) }
                clearEditTextFocus()
                findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
            }else{
                activity?.let { AndroidUtils.showSnackBar(it, getString(R.string.no_account_with_email)) }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clearEditTextFocus(){
        binding.forgotEmailEt.clearFocus()
    }

    override fun onClick(view: View?) {

        val isOnline = context?.let { NetworkUtils.isNetworkAvailable(it) }

        if(isOnline!!) {
            when (view?.id) {
                R.id.forgot_send_reset_pass_btn -> {

                    //forgotPasswordViewModel.getResetPasswordEmailStatus().observe(viewLifecycleOwner, resetEmailStatusObserver)

                    AndroidUtils.hideKeyboard(requireActivity())

                    val email = binding.forgotEmailEt.text?.trim().toString()

                    if (AndroidUtils.isValidEmail(email)) {
                        //forgotPasswordViewModel.sendPasswordResetEmail(email)
                    } else {
                        activity?.let {
                            AndroidUtils.showSnackBar(
                                it,
                                getString(R.string.invalid_email_err_msg)
                            )
                        }
                    }
                }
            }
        }else{
            activity?.let { AndroidUtils.showSnackBar(it, getString(R.string.not_connected_internet)) }
        }
    }


}
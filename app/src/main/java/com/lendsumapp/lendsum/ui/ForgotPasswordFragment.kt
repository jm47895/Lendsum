package com.lendsumapp.lendsum.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.lendsumapp.lendsum.R
import kotlinx.android.synthetic.main.fragment_forgot_password.*

class ForgotPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forgot_send_code_btn.setOnClickListener {

        }

        forgot_enter_code_btn.setOnClickListener {
            hideCodeVerificationUI()
            showResetPasswordUI()
        }

        forgot_reset_pass_btn.setOnClickListener {
            view.findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
        }
    }

    private fun hideCodeVerificationUI(){
        forgot_send_code_btn.visibility = View.INVISIBLE
        forgot_email_et.visibility = View.INVISIBLE
        forgot_enter_code_btn.visibility = View.INVISIBLE
        forgot_code_et.visibility = View.INVISIBLE
    }

    private fun showResetPasswordUI(){
        forgot_new_pass_et.visibility = View.VISIBLE
        forgot_match_new_pass_et.visibility = View.VISIBLE
        forgot_reset_pass_btn.visibility = View.VISIBLE
    }


}
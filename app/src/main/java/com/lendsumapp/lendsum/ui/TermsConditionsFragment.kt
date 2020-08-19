package com.lendsumapp.lendsum.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.lendsumapp.lendsum.R
import kotlinx.android.synthetic.main.fragment_number_verification.*
import kotlinx.android.synthetic.main.fragment_terms_conditions.*

class TermsConditionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms_conditions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        terms_back_btn.setOnClickListener {
            view.findNavController().navigate(R.id.action_termsConditionsFragment_to_numberVerificationFragment)
        }

        terms_next_btn.setOnClickListener {
            view.findNavController().navigate(R.id.action_termsConditionsFragment_to_financialInfoFragment)
        }
    }
}
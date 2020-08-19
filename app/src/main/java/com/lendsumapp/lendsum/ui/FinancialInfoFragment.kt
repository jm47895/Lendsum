package com.lendsumapp.lendsum.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.lendsumapp.lendsum.R
import kotlinx.android.synthetic.main.fragment_financial_info.*
import kotlinx.android.synthetic.main.fragment_login.*

class FinancialInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_financial_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        financial_info_next_btn.setOnClickListener {
            view.findNavController().navigate(R.id.action_financialInfoFragment_to_marketplaceFragment)
        }

        financial_info_back_btn.setOnClickListener {
            view.findNavController().navigate(R.id.action_financialInfoFragment_to_termsConditionsFragment)
        }
    }
}
package com.lendsumapp.lendsum.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.databinding.FragmentFinancialInfoBinding
import com.lendsumapp.lendsum.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinancialInfoFragment : Fragment() {

    private var _binding: FragmentFinancialInfoBinding? = null
    private val binding get() =  _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        _binding = FragmentFinancialInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.financialInfoNextBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_financialInfoFragment_to_marketplaceFragment)
        }

        binding.financialInfoBackBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_financialInfoFragment_to_termsConditionsFragment)
        }
    }
}
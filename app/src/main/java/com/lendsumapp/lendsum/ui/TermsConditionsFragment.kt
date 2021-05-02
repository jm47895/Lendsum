package com.lendsumapp.lendsum.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.databinding.FragmentFinancialInfoBinding
import com.lendsumapp.lendsum.databinding.FragmentTermsConditionsBinding

class TermsConditionsFragment : Fragment() {

    private var _binding: FragmentTermsConditionsBinding? = null
    private val binding get() =  _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        _binding = FragmentTermsConditionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.termsBackBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_termsConditionsFragment_to_numberVerificationFragment)
        }

        binding.termsNextBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_termsConditionsFragment_to_financialInfoFragment)
        }
    }
}
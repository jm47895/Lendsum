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
import com.lendsumapp.lendsum.databinding.FragmentNumberVerificationBinding
import com.lendsumapp.lendsum.databinding.FragmentProfileBinding
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.NAV_SIGN_UP_TYPE
import com.lendsumapp.lendsum.util.GlobalConstants.NUMBER_VERIFIED
import com.lendsumapp.lendsum.util.NavSignUpType
import com.lendsumapp.lendsum.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*

@AndroidEntryPoint
class ProfileFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private val sharedPrefs by lazy { activity?.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE) }
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.profileLogoutBtn?.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.profile_logout_btn -> {
                sharedPrefs?.edit()?.putBoolean(NUMBER_VERIFIED, false)?.apply()

                when(sharedPrefs?.getInt(NAV_SIGN_UP_TYPE, NavSignUpType.EMAIL_LOGIN.ordinal)){
                    NavSignUpType.EMAIL_LOGIN.ordinal ->{
                        profileViewModel.logOutOfEmailAndPass()
                        view.findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                    }
                    NavSignUpType.GOOGLE_LOGIN.ordinal ->{
                        context?.let {
                            profileViewModel.configureGoogleAuth()
                            profileViewModel.logOutOfGoogle()
                            view.findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                        }
                    }
                    NavSignUpType.FACEBOOK_LOGIN.ordinal ->{
                        profileViewModel.logOutOfFacebook()
                        view.findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                    }
                }
            }
        }
    }
}
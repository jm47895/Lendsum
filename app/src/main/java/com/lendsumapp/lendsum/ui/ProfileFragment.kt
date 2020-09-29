package com.lendsumapp.lendsum.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.databinding.FragmentProfileBinding
import com.lendsumapp.lendsum.util.GlobalConstants.NAV_SIGN_UP_TYPE
import com.lendsumapp.lendsum.util.GlobalConstants.NUMBER_VERIFIED
import com.lendsumapp.lendsum.util.NavSignUpType
import com.lendsumapp.lendsum.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment(), View.OnClickListener {

    private val firebaseUser = FirebaseAuth.getInstance().currentUser
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

        binding?.profileSettingsBtn?.setOnClickListener(this)
        binding?.profileName?.text = firebaseUser?.displayName
        binding?.profileUsername?.text = "@" + firebaseUser?.displayName
        binding?.profileKarmaScore?.text = firebaseUser?.email

        Glide.with(this)
            .applyDefaultRequestOptions(RequestOptions()
                .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                .error(R.drawable.com_facebook_profile_picture_blank_portrait).circleCrop())
            .load(firebaseUser?.photoUrl)
            .circleCrop()
            .into(binding?.profilePicImage!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.profile_settings_btn->{
                view.findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
            }
        }
    }
}
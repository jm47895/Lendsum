package com.lendsumapp.lendsum.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.databinding.FragmentProfileBinding
import com.lendsumapp.lendsum.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileViewModel.getCachedUser()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileSettingsBtn.setOnClickListener(this)
        binding.profileProfileEditBtn.setOnClickListener(this)

        profileViewModel.getCachedUser().observe(viewLifecycleOwner, Observer { user->
            binding.profileName.text = user.name
            binding.profileUsername.text = user.username
            binding.profileKarmaScore.text = getString(R.string.karma_score, user.karmaScore)

            Glide.with(this)
                .asBitmap()
                .load(user.profilePicUri)
                .apply(RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    //.error(R.drawable.com_facebook_profile_picture_blank_portrait)
                    .placeholder(R.drawable.ic_close_24))
                .circleCrop()
                .into(binding.profilePicImage)
        })

        binding.profilePicImage.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.profile_profile_edit_btn->{
                view.findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            }
            R.id.profile_settings_btn->{
                view.findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
            }
        }
    }

    companion object{
        private val TAG = ProfileFragment::class.simpleName
    }
}
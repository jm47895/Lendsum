package com.lendsumapp.lendsum.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.data.model.User
import com.lendsumapp.lendsum.databinding.FragmentProfileBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private val sharedPrefs by lazy { activity?.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE) }
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var userObserver: Observer<User>
    @Inject lateinit var androidUtils: AndroidUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(androidUtils.doesDatabaseExist(requireContext(), "lendsum.db")){
            Log.d(TAG, "Database Exists")
            profileViewModel.getCachedUser()
        }else{
            //TODO Read in remote database and cache responses
            Log.d(TAG, "Database does not Exists")
        }
    }

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

        userObserver = Observer { user ->

            binding?.profileName?.text = user.name
            binding?.profileUsername?.text = user.username
            binding?.profileKarmaScore?.text = user.karmaScore.toString()

            Glide.with(this)
                .applyDefaultRequestOptions(
                    RequestOptions()
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .error(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .circleCrop()
                )
                .load(user.profilePicUrl)
                .circleCrop()
                .into(binding?.profilePicImage!!)
        }
        profileViewModel.getUser().observe(viewLifecycleOwner, userObserver)
    }

    override fun onStop() {
        super.onStop()
        profileViewModel.getUser().removeObserver(userObserver)
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

    companion object{
        private val TAG = ProfileFragment::class.simpleName
    }
}
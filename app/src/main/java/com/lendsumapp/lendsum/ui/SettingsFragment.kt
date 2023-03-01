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
import com.lendsumapp.lendsum.databinding.FragmentSettingsBinding
import com.lendsumapp.lendsum.util.AndroidUtils
import com.lendsumapp.lendsum.util.AndroidUtils.Companion.editSharedPrefs
import com.lendsumapp.lendsum.util.GlobalConstants
import com.lendsumapp.lendsum.util.GlobalConstants.NAV_SIGN_UP_TYPE
import com.lendsumapp.lendsum.util.GlobalConstants.NUMBER_VERIFIED
import com.lendsumapp.lendsum.util.GlobalConstants.RETURNING_USER
import com.lendsumapp.lendsum.util.NavSignUpType
import com.lendsumapp.lendsum.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val sharedPrefs by lazy { activity?.getSharedPreferences(R.string.app_name.toString(), Context.MODE_PRIVATE) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingsLogoutBtn.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.settings_logout_btn->{
                editSharedPrefs(sharedPrefs!!, RETURNING_USER, false)
                editSharedPrefs(sharedPrefs!!, NUMBER_VERIFIED, false)

                when(sharedPrefs?.getInt(NAV_SIGN_UP_TYPE, NavSignUpType.EMAIL_LOGIN.ordinal)){
                    NavSignUpType.EMAIL_LOGIN.ordinal ->{
                        settingsViewModel.logOut()
                        view.findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
                    }
                    NavSignUpType.GOOGLE_LOGIN.ordinal ->{
                        context?.let {
                            //settingsViewModel.configureGoogleAuth(it)
                            settingsViewModel.logOut()
                            view.findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = SettingsFragment::class.simpleName
    }
}
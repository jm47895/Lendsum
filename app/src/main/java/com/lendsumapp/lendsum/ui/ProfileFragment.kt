package com.lendsumapp.lendsum.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.auth.GoogleAuthComponent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject lateinit var googleAuthComponent: GoogleAuthComponent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_logout_btn.setOnClickListener {

            context?.let {
                googleAuthComponent.configureGoogleAuth(it, getString(R.string.default_web_client_id))
                googleAuthComponent.signOutOfGoogle()
            }
            view.findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }
}
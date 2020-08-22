package com.lendsumapp.lendsum.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import com.lendsumapp.lendsum.R
import com.lendsumapp.lendsum.util.GlobalConstants
import kotlinx.coroutines.flow.callbackFlow

class MarketplaceFragment : Fragment(){

    private val sharedPrefs by lazy { activity?.getPreferences(Context.MODE_PRIVATE) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_marketplace, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs?.edit()?.putBoolean(GlobalConstants.returningUser, true)?.apply()
    }
}
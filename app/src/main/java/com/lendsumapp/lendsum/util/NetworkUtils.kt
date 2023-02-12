package com.lendsumapp.lendsum.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.os.Build
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class NetworkUtils{

    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            return connectivityManager.getNetworkCapabilities(network)?.hasCapability(NET_CAPABILITY_INTERNET) ?: false
        }
    }
}
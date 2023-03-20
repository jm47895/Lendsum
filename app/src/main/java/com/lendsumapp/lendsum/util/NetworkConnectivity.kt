package com.lendsumapp.lendsum.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.*
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectivity @Inject constructor(
    @ApplicationContext val context: Context
): NetworkObserver {

    override fun observe(): Flow<NetworkObserver.NetworkState> {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return callbackFlow {

            val callback = object: ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    trySend(NetworkObserver.NetworkState.AVAILABLE)
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    //trySend(NetworkObserver.NetworkState.LOSING)
                }

                override fun onLost(network: Network) {
                    trySend(NetworkObserver.NetworkState.LOST)
                }

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    if(networkCapabilities.hasCapability(NET_CAPABILITY_NOT_METERED) && networkCapabilities.hasCapability(NET_CAPABILITY_VALIDATED)){
                        trySend(NetworkObserver.NetworkState.AVAILABLE)
                    }
                }

                override fun onUnavailable() {
                    trySend(NetworkObserver.NetworkState.UNAVAILABLE)
                }
            }

            //I could use the registerDefaultNetworkCallback but it only works for Android O and above.
            connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder()
                    .addTransportType(TRANSPORT_WIFI)
                    .addTransportType(TRANSPORT_CELLULAR)
                    .addCapability(NET_CAPABILITY_INTERNET)
                    .addCapability(NET_CAPABILITY_VALIDATED)
                    .build(), callback)


            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }

    companion object{
        private val TAG = NetworkConnectivity::class.java.simpleName
    }

}

interface NetworkObserver{
    fun observe(): Flow<NetworkState>

    enum class NetworkState{
        AVAILABLE, UNAVAILABLE, LOSING, LOST
    }
}
package com.asemlab.quakes.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: NetworkCallback

    override fun onResume() {
        super.onResume()

        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request =
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

        networkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                onAvailableNetwork()
            }


            override fun onUnavailable() {
                super.onUnavailable()
                onUnavailableNetwork()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                onLost()
            }
        }


        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    abstract fun onAvailableNetwork()

    abstract fun onUnavailableNetwork()

    abstract fun onLost()

    override fun onStop() {
        super.onStop()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


}
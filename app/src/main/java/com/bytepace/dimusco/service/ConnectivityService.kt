package com.bytepace.dimusco.service

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import com.bytepace.dimusco.data.repository.UserRepository
//import com.bytepace.dimusco.data.repository.UserRepository
import com.bytepace.dimusco.data.source.remote.socket.SocketService

@Suppress("DEPRECATION")
class ConnectivityService private constructor() {

    companion object {
        val isConnected: Boolean
            @SuppressLint("MissingPermission")
            get() {
                if (::manager.isInitialized) {
                    return manager.activeNetworkInfo?.isConnected ?: false
                }

                throw UninitializedPropertyAccessException(
                    "Call init(Context) before using this method."
                )
            }

        private lateinit var manager: ConnectivityManager

        @SuppressLint("MissingPermission")
        fun init(context: Context) {
            manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            manager.registerNetworkCallback(
                NetworkRequest.Builder().build(),
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        UserRepository.get().getSession()?.let { SocketService.connect(it) }
                        Log.e("L-###-CON", "Available")
                    }

                    override fun onLosing(network: Network, maxMsToLive: Int) {
                        SocketService.disconnect()
                        Log.e("L-###-CON", "Losing")
                    }

                    override fun onLost(network: Network) {
                        SocketService.disconnect()
                        Log.e("L-###-CON", "Lost")
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        Log.e("L-###-CON", "Unavailable")
                    }
                })
        }
    }
}
package com.softnauts.visionauts.common

import javax.inject.Inject

/**
 * Class used to check if device is online.
 */
class ConnectivityManagerImpl @Inject constructor(
        private val connectivityManager: android.net.ConnectivityManager): ConnectivityManager {

    /**
     * Returns true when device is online, false when device is offline.
     */
    override fun isNetworkAvailable(): Boolean {
        connectivityManager.apply {
            val networkInfo = this.activeNetworkInfo
            return (networkInfo != null && networkInfo.isConnected)
        }
    }

}
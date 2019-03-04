package com.softnauts.visionauts.common

/**
 * Interface used to check if device is online.
 */
interface ConnectivityManager {
    /**
     * Returns true when device is online, false when device is offline.
     */
    fun isNetworkAvailable(): Boolean
}
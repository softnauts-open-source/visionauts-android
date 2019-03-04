package com.softnauts.visionauts.common

/**
 * Interface used to communicate with bluetooth module.
 */
interface BluetoothHelper {
    /**
     * Method used to check if bluetooth is enabled.
     */
    fun isEnabled(): Boolean

    /**
     * Method used to turn on bluetooth.
     */
    fun turnOnBluetooth()
}
package com.softnauts.visionauts.common

import android.bluetooth.BluetoothAdapter
import timber.log.Timber
import javax.inject.Inject

/**
 * Helper class used to communicate with bluetooth module.
 */
class BluetoothHelperImpl @Inject constructor(): BluetoothHelper {
    /**
     * Method used to check if bluetooth is enabled.
     */
    override fun isEnabled(): Boolean = BluetoothAdapter.getDefaultAdapter()?.isEnabled ?: false

    /**
     * Method used to turn on bluetooth.
     */
    override fun turnOnBluetooth() {
        BluetoothAdapter.getDefaultAdapter()?.run {
            if (isEnabled.not()) {
                enable()
            }
        } ?: run {
            Timber.e("Device does not support Bluetooth")
        }
    }
}
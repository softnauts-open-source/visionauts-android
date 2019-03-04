package com.softnauts.visionauts.common

/**
 * Interface used to handle changing bluetooth state.
 */
interface BluetoothCallbacks {
    /**
     * Called when bluetooth is turned on.
     */
    fun onBluetoothOn()

    /**
     * Called when bluetooth is turned off.
     */
    fun onBluetoothOff()
}
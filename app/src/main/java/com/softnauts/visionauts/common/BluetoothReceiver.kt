package com.softnauts.visionauts.common

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Class used to monitoring bluetooth state.
 */
class BluetoothReceiver(private val bluetoothCallbacks: BluetoothCallbacks) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (BluetoothAdapter.ACTION_STATE_CHANGED == intent.action) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
            when (state) {
                BluetoothAdapter.STATE_ON -> bluetoothCallbacks.onBluetoothOn()
                BluetoothAdapter.STATE_OFF -> bluetoothCallbacks.onBluetoothOff()
            }
        }
    }
}
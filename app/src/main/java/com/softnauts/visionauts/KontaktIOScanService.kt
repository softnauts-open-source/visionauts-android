package com.softnauts.visionauts

import com.kontakt.sdk.android.ble.configuration.ScanMode
import com.kontakt.sdk.android.ble.configuration.ScanPeriod
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener
import com.kontakt.sdk.android.common.Proximity
import com.kontakt.sdk.android.common.profile.IBeaconDevice
import com.kontakt.sdk.android.common.profile.IBeaconRegion
import com.softnauts.visionauts.data.datasource.SettingsDataSource
import javax.inject.Inject

/**
 * Scan service implementation class for Kontakt.io beacons.
 */
class KontaktIOScanService : BeaconScanService(), IBeaconListener {

    @Inject
    lateinit var settingsDataSource: SettingsDataSource

    private lateinit var proximityManager: ProximityManager

    override fun getDataSource(): SettingsDataSource = settingsDataSource

    override fun setupSDKScanner() {
        proximityManager = ProximityManagerFactory.create(this).apply {
            configuration()
                ?.scanPeriod(ScanPeriod.RANGING)
                ?.scanMode(ScanMode.BALANCED)

            setIBeaconListener(this@KontaktIOScanService)
        }
    }

    override fun connectSDKScanner() {
        proximityManager.connect {
            proximityManager.startScanning()
        }
    }

    override fun disconnectSDKScanner() {
        proximityManager.disconnect()
    }

    /**
     * Method called when beacon device is lost.
     */
    override fun onIBeaconLost(iBeacon: IBeaconDevice, region: IBeaconRegion?) {
        iBeacon.uniqueId?.apply {
            onDeviceLost(this, iBeacon.minor.toString(), iBeacon.major.toString())
        }
    }

    /**
     * Method called when beacon device is updated.
     */
    override fun onIBeaconsUpdated(iBeacons: MutableList<IBeaconDevice>?, region: IBeaconRegion?) {
        iBeacons?.forEach {
            splitByDistance(it)
        }
    }

    /**
     * Method called when new beacon device is discovered.
     */
    override fun onIBeaconDiscovered(iBeacon: IBeaconDevice, region: IBeaconRegion?) {
        splitByDistance(iBeacon)
    }

    /***
     * This method decides whether beacon is discovered or lost based on its approximated distance.
     * Distance is approximated using the following algorithm:
     * 10 ^ ((-69 – (RSSI)) / (10 * N)), where N is in {2, 3} and RSSI is a negative value.
     * @param beacon beacon to analyze
     * @param maxDistance distance to compare against.
     * If approximated distance is greater than maxDistance beacon is treated as lost, else as discovered.
     */
    private fun splitByDistance(beacon: IBeaconDevice) {
        if (beacon.uniqueId == null) {
            return
        }
        val approxDistance = approximateDistance(beacon.rssi)
        if (approxDistance <= maxDistance && beacon.nearOrImmediate()) {
            onDeviceDiscovered(beacon.uniqueId, beacon.minor.toString(), beacon.major.toString())
        } else {
            onDeviceLost(beacon.uniqueId, beacon.minor.toString(), beacon.major.toString())
        }
    }

    /**
     * Method used to check if beacon device proximity equals NEAR or IMMEDIATE.
     * Returns true when proximity equals NEAR or IMMEDIATE, otherwise returns false.
     */
    private fun IBeaconDevice.nearOrImmediate(): Boolean {
        return proximity == Proximity.NEAR || proximity == Proximity.IMMEDIATE
    }

    /**
     * Method used to calculate approximate distance.
     */
    private fun approximateDistance(rssi: Int): Double {
        val difference = -30.0
        return Math.pow(10.0, (RSSI_1M - difference - (rssi)) / (10.0 * N))
    }

    companion object {
        private const val N = 2.0
        private const val RSSI_1M = -115
    }
}
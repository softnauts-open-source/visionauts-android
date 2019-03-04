package com.softnauts.visionauts

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.softnauts.visionauts.common.BeaconsStateEventEmitter
import com.softnauts.visionauts.data.datasource.SettingsDataSource
import com.softnauts.visionauts.data.model.RangeSettingsModel
import com.softnauts.visionauts.data.model.SimpleBeaconModel
import dagger.android.DaggerService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

/**
 * Abstract service class used to background beacons scan.
 */
abstract class BeaconScanService : DaggerService() {
    protected var maxDistance: Float = MAX_DISTANCE_DEFAULT_VALUE
    private val deviceLostTasks = mutableMapOf<String, Runnable>()
    private val handler = Handler()
    private var isRunning: Boolean = false
    private val eventsEmitter = BeaconsStateEventEmitter()

    /**
     * Method used to init scanner sdk.
     */
    abstract fun setupSDKScanner()

    /**
     * Method used to start scanner sdk.
     */
    abstract fun connectSDKScanner()

    /**
     * Method used to release scanner sdk.
     */
    abstract fun disconnectSDKScanner()

    /**
     * Method used to get settings data source.
     */
    abstract fun getDataSource(): SettingsDataSource

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()
        setupSDKScanner()
        isRunning = false
        EventBus.getDefault().register(this)
        getDataSource().getRange().subscribe({ it ->
            it?.apply { maxDistance = it }
        }, { t ->
            Timber.e(t)
        })
    }

    override fun onDestroy() {
        disconnectSDKScanner()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            val builder = Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.working))
                .setAutoCancel(true)

            val notification = builder.build()
            startForeground(1, notification)
        } else {
            val builder = NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.working))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
            val notification = builder.build()
            startForeground(1, notification)
        }
        if (!isRunning) {
            connectSDKScanner()
            isRunning = true
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * Method called when user sets and saves new values in SettingsActivity.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSettingsChanged(rangeSettingsModel: RangeSettingsModel) {
        this.maxDistance = rangeSettingsModel.range
    }

    /**
     * Method called when new beacon device is discovered.
     */
    protected fun onDeviceDiscovered(uuid: String, minor: String, major: String) {
        deviceLostTasks[uuid]?.apply {
            handler.removeCallbacks(this)
        }
        deviceLostTasks.remove(uuid)
        eventsEmitter.emmitBeaconFoundEvent(SimpleBeaconModel(uuid, minor, major))
    }

    /**
     * Method called when beacon device is lost.
     */
    protected fun onDeviceLost(uuid: String, minor: String, major: String) {
        val runnable = Runnable {
            eventsEmitter.emmitBeaconLostEvent(SimpleBeaconModel(uuid, minor, major))
        }
        handler.postDelayed(runnable, 1000)
        deviceLostTasks[uuid] = runnable
    }

    companion object {
        private const val MAX_DISTANCE_DEFAULT_VALUE = 2.5f
        const val CHANNEL_ID = BuildConfig.APPLICATION_ID + "_notifications"
    }

}
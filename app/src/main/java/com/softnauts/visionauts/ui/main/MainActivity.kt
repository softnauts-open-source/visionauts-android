package com.softnauts.visionauts.ui.main

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.view.View.*
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.softnauts.visionauts.KontaktIOScanService
import com.softnauts.visionauts.R
import com.softnauts.visionauts.common.BeaconContentHelper
import com.softnauts.visionauts.common.BluetoothCallbacks
import com.softnauts.visionauts.common.BluetoothReceiver
import com.softnauts.visionauts.common.ViewModelFactory
import com.softnauts.visionauts.data.model.MainActivityViewState
import com.softnauts.visionauts.events.BeaconFoundEvent
import com.softnauts.visionauts.events.BeaconLostEvent
import com.softnauts.visionauts.ui.help.HelpActivity
import com.softnauts.visionauts.ui.main.MainViewModel.Companion.NONE
import com.softnauts.visionauts.ui.settings.SettingsActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject

/**
 * Main activity class.
 */
class MainActivity : DaggerAppCompatActivity(), BluetoothCallbacks, TextToSpeech.OnInitListener {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var beaconContentHelper: BeaconContentHelper
    private lateinit var viewModel: MainViewModel
    private lateinit var bluetoothReceiver: BluetoothReceiver
    private var serviceIntent: Intent? = null
    private lateinit var textToSpeech : TextToSpeech
    private var canSpeak = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        checkPermissionsAndStartBackgroundService()

        bluetoothReceiver = BluetoothReceiver(this)
        registerReceiver(bluetoothReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

        EventBus.getDefault().register(this)

        viewModel = ViewModelProviders.of(
            this,
            viewModelFactory
        ).get(MainViewModel::class.java)

        textToSpeech = TextToSpeech(this, this)
        textToSpeech.language = Locale.ENGLISH

        setupViews()
        subscribe()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onActivityResumed()
    }

    /**
     * Method called when TextToSpeech is initialized successfully.
     */
    override fun onInit(status: Int) {
        canSpeak = true
    }

    /**
     * Method used to register subscription to viewmodel's live data.
     */
    private fun subscribe() {
        viewModel.viewState.observe(this, Observer { it ->
            it?.apply {
                when (this) {
                    MainActivityViewState.BLUETOOTH -> showBluetoothLayout()
                    MainActivityViewState.DETECTING -> showDetectingLayout()
                    MainActivityViewState.BEACONS -> showBeaconsLayout()
                }
            }
        })
        viewModel.currentItemLiveData.observe(this, Observer { it ->
            it?.getContentIfNotHandled()?.texts?.apply {
                beaconContentHelper.getText(this).apply {
                    currentItem.text = this
                    speak(this)
                }
            }
        })
        viewModel.currentItemPosition.observe(this, Observer { it ->
            it?.apply {
                val itemsCount = viewModel.itemsCount()
                when {
                    this == NONE -> {
                        buttonPrevious.visibility = GONE
                        buttonNext.visibility = GONE
                    }
                    itemsCount <= 1 -> {
                        buttonPrevious.visibility = GONE
                        buttonNext.visibility = GONE
                    }
                    this == 0 -> {
                        buttonPrevious.visibility = GONE
                        buttonNext.visibility = VISIBLE
                    }
                    this == itemsCount - 1 -> {
                        buttonPrevious.visibility = VISIBLE
                        buttonNext.visibility = GONE
                    }
                    else -> {
                        buttonPrevious.visibility = VISIBLE
                        buttonNext.visibility = VISIBLE
                    }
                }
            }
        })
    }

    /**
     * Method used to unregister subscription to viewmodel's live data.
     */
    private fun unsubscirbe() {
        viewModel.viewState.removeObservers(this)
        viewModel.currentItemPosition.removeObservers(this)
        viewModel.currentItemLiveData.removeObservers(this)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    startBackgroundService()
                } else {
                    showNeedPermissionsDialog()
                }
            }
        }
    }

    /**
     * Method checks if required permissions are granted and if granted starts background service, otherwise asks for permissions.
     */
    private fun checkPermissionsAndStartBackgroundService() {
        // in this example we are using Kontakt.io SDK (https://kontakt.io/). You can customize this app for your own
        // purposes using different provider's SDK (for example Estimote).
        // while using SDK different than Kontakt.io - create own implementation of BeaconScanService and use it here instead of KontaktIOScanService.
        serviceIntent = Intent(applicationContext, KontaktIOScanService::class.java)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startBackgroundService()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onDestroy() {
        textToSpeech.shutdown()
        unregisterReceiver(bluetoothReceiver)
        EventBus.getDefault().unregister(this)
        stopBackgroundService()
        unsubscirbe()
        super.onDestroy()
    }

    /**
     * Method called when bluetooth is turned on.
     */
    override fun onBluetoothOn() {
        viewModel.onBluetoothOn()
    }

    /**
     * Method called when bluetooth is turned off.
     */
    override fun onBluetoothOff() {
        viewModel.onBluetoothOff()
    }

    /**
     * Method called when background service finds new beacon.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewBeaconEvent(beaconFoundEvent: BeaconFoundEvent) {
        viewModel.onBeaconFound(beaconFoundEvent.simpleBeaconModel)
    }

    /**
     * Method called when background service lost beacon that was in our range.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewBeaconEvent(beaconFoundEvent: BeaconLostEvent) {
        viewModel.onBeaconLost(beaconFoundEvent.simpleBeaconModel)
    }

    /**
     * Method used to start background serice.
     */
    private fun startBackgroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    /**
     * Method used to stop background service.
     */
    private fun stopBackgroundService() {
        stopService(serviceIntent)
    }

    /**
     * Method used to set views values, listeners etc.
     */
    private fun setupViews() {
        pulsator.start()
        buttonHelp.setOnClickListener { HelpActivity.start(this) }
        buttonSettings.setOnClickListener { SettingsActivity.start(this) }
        buttonPrevious.setOnClickListener {
            viewModel.onPreviousClicked()
        }
        buttonNext.setOnClickListener {
            viewModel.onNextClicked()
        }
        buttonPlay.setOnClickListener {
            onPlayClicked()
        }
    }

    /**
     * Method used to show dialog when permissions are not granted.
     */
    private fun showNeedPermissionsDialog() {
        MaterialDialog(this).show {
            title(R.string.error)
            message(R.string.location_permissions_required)
            positiveButton(R.string.go_to_settings) { dialog ->
                openPermissionsSettings()
            }
            negativeButton(R.string.cancel) { dialog ->
                finish()
            }
        }
    }

    /**
     * Method used to navigate to device settings with Visionauts application page.
     */
    private fun openPermissionsSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    /**
     * Method used to show detecting views group.
     */
    private fun showDetectingLayout() {
        detectingGroup.visibility = VISIBLE
        bluetoothGroup.visibility = GONE
        beaconsGroup.visibility = INVISIBLE
    }

    /**
     * Method used to show bluetooth views group.
     */
    private fun showBluetoothLayout() {
        detectingGroup.visibility = GONE
        bluetoothGroup.visibility = VISIBLE
        beaconsGroup.visibility = INVISIBLE
    }

    /**
     * Method used to show beacons views group.
     */
    private fun showBeaconsLayout() {
        detectingGroup.visibility = GONE
        bluetoothGroup.visibility = GONE
        beaconsGroup.visibility = VISIBLE
    }

    /**
     * Method used to convert text into speech using TextToSpeech.
     */
    private fun speak(text: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null,null)
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    /**
     * Method called when play button is clicked.
     */
    private fun onPlayClicked(){
        viewModel.currentItemLiveData.value?.peekContent()?.texts?.apply {
            speak(beaconContentHelper.getText(this))
        }
    }

    companion object {
        /**
         * Method used to launch MainActivity.
         */
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }

        const val PERMISSIONS_REQUEST_LOCATION = 99

    }
}

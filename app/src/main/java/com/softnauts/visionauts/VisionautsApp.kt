package com.softnauts.visionauts

import android.content.Context
import androidx.multidex.MultiDex
import com.facebook.stetho.Stetho
import com.kontakt.sdk.android.common.KontaktSDK
import com.softnauts.visionauts.di.component.DaggerApplicationComponent
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

/**
 * Visionauts application class.
 */
class VisionautsApp : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        initializeLeakcanary()
        initializeStetho()
        initializeTimber()
        initializeBeaconsSDK()
    }

    /**
     * Method used by Dagger to build application component and init AndroidInjector.
     */
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerApplicationComponent.builder().create(this)

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    /**
     * Method used to initialize Stetho.
     */
    private fun initializeStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    /**
     * Method used to initialize Timber.
     */
    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    /**
     * Method used to initialize initializeLeakcanary.
     */
    private fun initializeLeakcanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    /**
     * Method used to initialize beacons SDK.
     */
    private fun initializeBeaconsSDK() {
        // in this example we are using Kontakt.io SDK (https://kontakt.io/). You can customize this app for your own
        // purposes using different provider's SDK (for example Estimote).
        // while using SDK different than Kontakt.io initialize SDK here, then create and use your own implementation of BeaconScanService.
        KontaktSDK.initialize("YOUR_API_KEY") // paste your API KEY here, if using Kontakt.io
    }
}
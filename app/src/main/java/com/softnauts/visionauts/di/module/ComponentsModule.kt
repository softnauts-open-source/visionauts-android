package com.softnauts.visionauts.di.module

import com.softnauts.visionauts.common.*
import dagger.Binds
import dagger.Module
/**
 * Class used by Dagger to provide components needed by app.
 */
@Module
interface ComponentsModule {
    @Binds
    fun bindConnectivityManager(managerImpl: ConnectivityManagerImpl): ConnectivityManager
    @Binds
    fun bindLocaleManager(managerImpl: LocaleManagerImpl): LocaleManager
    @Binds
    fun bindBluetoothHelper(bluetoothHelperImpl: BluetoothHelperImpl): BluetoothHelper
}
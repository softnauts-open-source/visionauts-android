package com.softnauts.visionauts.di.module

import com.softnauts.visionauts.data.datasource.BeaconDataSource
import com.softnauts.visionauts.data.repository.BeaconRepository
import com.softnauts.visionauts.data.datasource.SettingsDataSource
import com.softnauts.visionauts.data.repository.SettingsRepository
import dagger.Binds
import dagger.Module

/**
 * Class used by Dagger to provide data source implementations.
 */
@Module
interface DataModule {
    @Binds
    fun bindsBeaconDataSource(repository: BeaconRepository): BeaconDataSource

    @Binds
    fun bindsSettingsDataSource(repository: SettingsRepository): SettingsDataSource
}
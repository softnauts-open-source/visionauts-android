package com.softnauts.visionauts.di.module

import com.softnauts.visionauts.KontaktIOScanService
import com.softnauts.visionauts.ui.main.MainActivity
import com.softnauts.visionauts.ui.settings.SettingsActivity
import com.softnauts.visionauts.ui.init.InitActivity
import com.softnauts.visionauts.ui.init.InitScreenModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Module used by dagger support to inject dependencies into Activities, Fragments and Services.
 */
@Module
interface AndroidBindingModule {
    @ContributesAndroidInjector(modules = [InitScreenModule::class])
    fun splashActivity(): InitActivity
    @ContributesAndroidInjector
    fun mainActivity(): MainActivity
    @ContributesAndroidInjector
    fun settingsActivity(): SettingsActivity
    @ContributesAndroidInjector
    fun kontaktIoService(): KontaktIOScanService
}
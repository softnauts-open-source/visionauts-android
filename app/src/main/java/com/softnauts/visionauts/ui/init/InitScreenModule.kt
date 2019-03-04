package com.softnauts.visionauts.ui.init

import dagger.Binds
import dagger.Module

@Module
interface InitScreenModule {
    @Binds
    fun bindView(activity: InitActivity): InitView
    @Binds
    fun bindRouter(activity: InitActivity): InitRouter
}
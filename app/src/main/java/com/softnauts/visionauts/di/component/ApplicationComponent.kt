package com.softnauts.visionauts.di.component

import com.softnauts.visionauts.VisionautsApp
import com.softnauts.visionauts.di.module.*
import com.softnauts.visionauts.di.scope.ApplicationScope
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@ApplicationScope
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AndroidBindingModule::class,
    ApplicationModule::class,
    ViewModelModule::class,
    NetworkModule::class,
    ComponentsModule::class,
    DataModule::class,
    DatabaseModule::class
])
/**
 * Application component used by Dagger to merge all modules used by app.
 */
interface ApplicationComponent : AndroidInjector<VisionautsApp> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<VisionautsApp>()
}
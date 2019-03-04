package com.softnauts.visionauts.di.module

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.softnauts.visionauts.BuildConfig
import com.softnauts.visionauts.VisionautsApp
import com.softnauts.visionauts.di.scope.ApplicationScope
import dagger.Binds
import dagger.Module
import dagger.Provides

/**
 * Class used by Dagger to provide components needed by app.
 */
@Module
abstract class ApplicationModule {
    @Binds
    internal abstract fun bindContext(app: VisionautsApp): Context

    @Module
    companion object {
        @JvmStatic
        @Provides
        @ApplicationScope
        fun provideConnectivityManager(context: Context): ConnectivityManager {
            return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        @JvmStatic
        @Provides
        @ApplicationScope
        fun provideSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        }
    }
}
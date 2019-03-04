package com.softnauts.visionauts.di.module

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.softnauts.visionauts.BuildConfig
import com.softnauts.visionauts.common.NetworkStateInterceptor
import com.softnauts.visionauts.data.NetworkService
import com.softnauts.visionauts.di.scope.ApplicationScope
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

/**
 * Class used by Dagger to provide http client and Retrofit service instance.
 */
@Module
class NetworkModule {

    @Provides
    @ApplicationScope
    internal fun provideLoggingLevel(): HttpLoggingInterceptor.Level =
            when (BuildConfig.DEBUG) {
                true -> HttpLoggingInterceptor.Level.BODY
                else -> HttpLoggingInterceptor.Level.NONE
            }

    @Provides
    @ApplicationScope
    internal fun provideLoggingInterceptor(
            loggingLevel: HttpLoggingInterceptor.Level): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(loggingLevel)
    }

    @Provides
    @ApplicationScope
    internal fun provideStethoInterceptor(): StethoInterceptor {
        return StethoInterceptor()
    }

    @Provides
    @ApplicationScope
    @Inject
    internal fun provideHttpCache(context: Context): Cache {
        return Cache(context.cacheDir, CACHE_SIZE)
    }

    @Provides
    @ApplicationScope
    internal fun providesOkHttpClient(cache: Cache,
                                      networkStateInterceptor: NetworkStateInterceptor,
                                      loggingInterceptor: HttpLoggingInterceptor,
                                      stethoInterceptor: StethoInterceptor): OkHttpClient {

        return OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(networkStateInterceptor)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(stethoInterceptor)
                .build()
    }

    @Provides
    @ApplicationScope
    internal fun providesRx2JavaCallAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }

    @Provides
    @ApplicationScope
    internal fun providesDefaultGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @ApplicationScope
    internal fun providesDefaultGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }


    @Provides
    @ApplicationScope
    internal fun providesDefaultRetrofit(gsonConverterFactory: GsonConverterFactory,
                                         okHttpClient: OkHttpClient,
                                         rxJava2CallAdapterFactory: RxJava2CallAdapterFactory): Retrofit {

        return Retrofit.Builder().addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .baseUrl(BuildConfig.API_URL)
                .client(okHttpClient)
                .build()
    }

    @Provides
    @ApplicationScope
    internal fun provideApiService(retrofit: Retrofit): NetworkService {
        return retrofit.create<NetworkService>(
                NetworkService::class.java)
    }

    companion object {
        private const val CACHE_SIZE: Long = 10 * 1024 * 1024
    }

}
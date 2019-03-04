package com.softnauts.visionauts.data.repository

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.softnauts.visionauts.data.datasource.SettingsDataSource
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SettingsRepository @Inject constructor(private val sharedPreferences: SharedPreferences) :
    SettingsDataSource {
    /**
     * Method used to get actual range setting from shared preferences.
     */
    override fun getRange(): Observable<Float> {
        return Observable.fromCallable { sharedPreferences.getFloat(SETTINGS_RANGE_KEY, 2.5f) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Method used to save new range setting to shared preferences.
     */
    @SuppressLint("ApplySharedPref")
    override fun saveRange(range: Float): Single<Float> {
        return Single.fromCallable {
            sharedPreferences.edit().putFloat(SETTINGS_RANGE_KEY, range).commit()
            range
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    companion object {
        const val SETTINGS_RANGE_KEY = "settings_range"
    }
}
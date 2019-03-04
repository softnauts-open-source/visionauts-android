package com.softnauts.visionauts.data.datasource

import io.reactivex.Observable
import io.reactivex.Single

interface SettingsDataSource {
    /**
     * Returns saved locally range setting.
     */
    fun getRange(): Observable<Float>

    /**
     * Saves locally new range setting.
     */
    fun saveRange(range: Float): Single<Float>
}
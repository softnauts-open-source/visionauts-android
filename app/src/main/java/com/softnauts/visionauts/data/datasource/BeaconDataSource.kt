package com.softnauts.visionauts.data.datasource

import com.softnauts.visionauts.data.model.BeaconDto
import io.reactivex.Completable
import io.reactivex.Maybe

interface BeaconDataSource {
    /**
     * Checks if there is need to download beacons from api then returns
     * beacons list, or throws exception if there is no way to get data,
     * because app without beacons data is useless.
     */
    fun getBeaconsFromApiIfNeeded(): Completable

    /**
     * Returns BeaconDto with given uuid, minor and major.
     */
    fun getBeaconByIdentifier(uuid:String, minor: String, major:String): Maybe<BeaconDto>
}
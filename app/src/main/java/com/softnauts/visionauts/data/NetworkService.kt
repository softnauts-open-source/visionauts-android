package com.softnauts.visionauts.data

import com.softnauts.visionauts.data.model.BeaconDto
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Retrofit api interface used to connect to server rest API.
 */
interface NetworkService {
    /**
     * Method used to get beacons data from rest API.
     */
    @GET("api/beacons")
    fun getBeacons(): Single<List<BeaconDto>>
}
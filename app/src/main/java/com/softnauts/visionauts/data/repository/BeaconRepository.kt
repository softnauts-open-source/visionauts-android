package com.softnauts.visionauts.data.repository

import com.softnauts.visionauts.common.ConnectivityManager
import com.softnauts.visionauts.common.DownloadDataException
import com.softnauts.visionauts.data.NetworkService
import com.softnauts.visionauts.data.dao.BeaconsDao
import com.softnauts.visionauts.data.datasource.BeaconDataSource
import com.softnauts.visionauts.data.entity.BeaconEntity
import com.softnauts.visionauts.data.entity.BeaconTextContentEntity
import com.softnauts.visionauts.data.model.BeaconDto
import com.softnauts.visionauts.data.model.BeaconTextContentDto
import com.softnauts.visionauts.data.model.BeaconWithTexts
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Instant
import java.util.*
import javax.inject.Inject

class BeaconRepository @Inject constructor(
    private val networkService: NetworkService,
    private val connectivityManager: ConnectivityManager,
    private val beaconsDao: BeaconsDao
) : BeaconDataSource {

    /**
     * Checks if there is need to download beacons from api then returns
     * beacons list, or throws exception if there is no way to get data,
     * because app without beacons data is useless.
     */
    override fun getBeaconsFromApiIfNeeded(): Completable {
        return needToDownloadFromApi().flatMapCompletable { needToDownload ->
            if (needToDownload) {
                getBeaconsFromApi()
            } else {
                Completable.complete()
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Returns beacon data by uuid, minor and major.
     */
    override fun getBeaconByIdentifier(uuid: String, minor: String, major: String): Maybe<BeaconDto> {
        return beaconsDao.getBeaconByIdentifiers(uuid, minor, major).map {
            it -> mapBeaconWithTextsToBeaconModel(it)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Returns "true" if there is internet connection available,
     * returns "false" if there is no internet connection available and there are items in local database,
     * otherwise throws exception, because app without beacons data is useless.
     */
    private fun needToDownloadFromApi(): Single<Boolean> = if (connectivityManager.isNetworkAvailable()) {
        Single.just(true)
    } else {
        getBeaconCountFromDb().map { count ->
            if (count > 0) {
                false
            } else {
                throw DownloadDataException("Can not download beacons data from API also local database is empty.")
            }
        }
    }

    /**
     * Returns count of beacon items in local database.
     */
    private fun getBeaconCountFromDb(): Single<Int> = beaconsDao.getBeaconsCount()

    /**
     * Downloads beacons data from API
     */
    private fun getBeaconsFromApi() =
        networkService.getBeacons().flatMapCompletable { data -> deleteOldDataAndSaveBeaconsToDb(data) }

    /**
     * Deletes old data and saves new list of beacons to local database.
     */
    private fun deleteOldDataAndSaveBeaconsToDb(data: List<BeaconDto>): Completable {
        deleteOldData()
        return saveBeaconsToDb(data)
    }

    /**
     * Saves beacon data to local database.
     */
    private fun saveBeaconsToDb(data: List<BeaconDto>): Completable {
        beaconsDao.insert(mapBeaconModelToEntity(data))
        data.forEach {
            beaconsDao.insertBeaconTexts(mapBeaconTextModelToEntity(it.texts, it.id))
        }
        return Completable.complete()
    }

    /**
     * Deletes old beacons data from local database.
     */
    private fun deleteOldData() {
        beaconsDao.deleteAll()
        beaconsDao.deleteAllTexts()
    }

    /**
     * Maps list of BeaconDto to list of BeaconEntity
     */
    private fun mapBeaconModelToEntity(data: List<BeaconDto>): List<BeaconEntity> = data.map {
        BeaconEntity(
            it.id,
            it.uuid,
            it.minor,
            it.major,
            it.enabled,
            Instant.ofEpochMilli(it.createdAt.time),
            Instant.ofEpochMilli(it.updatedAt.time)
        )
    }

    /**
     * Maps list of BeaconTextContentDto to list of BeaconTextContentEntity
     */
    private fun mapBeaconTextModelToEntity(
        data: List<BeaconTextContentDto>,
        beaconId: Long
    ): List<BeaconTextContentEntity> = data.map {
        BeaconTextContentEntity(
            it.id,
            beaconId,
            it.language,
            it.description,
            Instant.ofEpochMilli(it.createdAt.time),
            Instant.ofEpochMilli(it.updatedAt.time)
        )
    }

    /**
     * Maps BeaconWithTexts to BeaconDto
     */
    private fun mapBeaconWithTextsToBeaconModel(it: BeaconWithTexts): BeaconDto {
        return BeaconDto(
            it.beaconEntity!!.id,
            it.beaconEntity!!.uuid,
            it.beaconEntity!!.minor,
            it.beaconEntity!!.major,
            it.beaconEntity!!.enabled,
            mapBeaconTextContentEntitiesToBeaconTextContentDtos(it.textEntities!!),
            Date(it.beaconEntity!!.createdAt.toEpochMilli()),
            Date(it.beaconEntity!!.updatedAt.toEpochMilli())
        )
    }

    /**
     * Maps list of BeaconTextContentEntity to list of BeaconTextContentDto
     */
    private fun mapBeaconTextContentEntitiesToBeaconTextContentDtos(data: List<BeaconTextContentEntity>): List<BeaconTextContentDto> {
        return data.map {
            BeaconTextContentDto(
                it.id,
                it.language,
                it.description,
                Date(it.createdAt.toEpochMilli()),
                Date(it.updatedAt.toEpochMilli())
            )
        }
    }
}
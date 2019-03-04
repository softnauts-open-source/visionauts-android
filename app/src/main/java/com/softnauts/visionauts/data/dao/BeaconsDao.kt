package com.softnauts.visionauts.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softnauts.visionauts.data.entity.BeaconEntity
import com.softnauts.visionauts.data.entity.BeaconTextContentEntity
import com.softnauts.visionauts.data.model.BeaconWithTexts
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface BeaconsDao {
    /**
     * Saves list of BeaconEntity to local database (Room).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(beaconEntities: List<BeaconEntity>)

    /**
     * Saves list of BeaconTextContentEntity to local database (Room).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBeaconTexts(beaconTextEntities: List<BeaconTextContentEntity>)

    /**
     * Returns BeaconEntity count.
     */
    @Query("SELECT COUNT(id) FROM beacon")
    fun getBeaconsCount(): Single<Int>

    /**
     * Returns BeaconWithTexts by uuid, minor and major.
     */
    @Query("SELECT * FROM beacon WHERE uuid = :uuid AND minor = :minor AND major = :major LIMIT 1")
    fun getBeaconByIdentifiers(uuid:String, minor: String, major:String): Maybe<BeaconWithTexts>

    /**
     * Deletes all BeaconEntity from local database (Room).
     */
    @Query("DELETE FROM beacon")
    fun deleteAll()

    /**
     * Deletes all BeaconTextContentEntity from local database (Room).
     */
    @Query("DELETE FROM beaconContent")
    fun deleteAllTexts()
}
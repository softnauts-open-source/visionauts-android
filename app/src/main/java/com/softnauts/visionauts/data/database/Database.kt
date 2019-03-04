package com.softnauts.visionauts.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.softnauts.visionauts.data.dao.BeaconsDao
import com.softnauts.visionauts.data.entity.BeaconEntity
import com.softnauts.visionauts.data.entity.BeaconTextContentEntity

@Database(
        entities = [
            BeaconEntity::class,
            BeaconTextContentEntity::class
        ],
        version = 1
)

@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun beaconsDao(): BeaconsDao
}
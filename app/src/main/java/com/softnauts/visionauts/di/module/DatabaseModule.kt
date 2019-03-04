package com.softnauts.visionauts.di.module

import android.content.Context
import androidx.room.Room
import com.softnauts.visionauts.data.dao.BeaconsDao
import com.softnauts.visionauts.data.database.Database
import com.softnauts.visionauts.di.scope.ApplicationScope
import dagger.Module
import dagger.Provides

/**
 * Class used by Dagger to provide database (Room).
 */
@Module
abstract class DatabaseModule {
    @Module
    companion object {
        @JvmStatic
        @ApplicationScope
        @Provides
        internal fun provideDatabase(context: Context): Database {
            return Room.databaseBuilder(context, Database::class.java,
                "visionauts-db")
                .fallbackToDestructiveMigration()
                .build()
        }

        @JvmStatic
        @ApplicationScope
        @Provides
        internal fun provideBeaconsDao(database: Database): BeaconsDao {
            return database.beaconsDao()
        }
    }
}
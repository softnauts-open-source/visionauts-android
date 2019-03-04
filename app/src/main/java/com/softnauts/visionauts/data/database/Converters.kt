package com.softnauts.visionauts.data.database

import androidx.room.TypeConverter
import android.net.Uri
import org.threeten.bp.Instant

/**
 * Class used to map types not supported by Room to be able to storage them in Room.
 */
class Converters {

    @TypeConverter
    fun fromUri(uri: Uri): String = uri.toString()

    @TypeConverter
    fun toUri(string: String): Uri = Uri.parse(string)

    @TypeConverter
    fun fromInstant(instant: Instant): Long = instant.epochSecond

    @TypeConverter
    fun toInstant(long: Long): Instant = Instant.ofEpochSecond(long)

}
package com.softnauts.visionauts.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.Instant

/**
 * Beacon entity class.
 */
@Entity(tableName = "beacon")
data class BeaconEntity(
    @PrimaryKey
    val id: Long,
    val uuid: String,
    val minor: String,
    val major: String,
    val enabled: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)
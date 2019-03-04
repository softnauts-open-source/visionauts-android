package com.softnauts.visionauts.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.threeten.bp.Instant

@Entity(
    tableName = "beaconContent",
    foreignKeys = [ForeignKey(
        entity = BeaconEntity::class, parentColumns = arrayOf("id"),
        childColumns = arrayOf("beaconId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
/**
 * Beacon content entity class.
 */
data class BeaconTextContentEntity(
    @PrimaryKey
    val id: Long,
    val beaconId: Long,
    val language: String,
    val description: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
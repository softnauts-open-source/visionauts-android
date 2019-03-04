package com.softnauts.visionauts.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Beacon content data transfer object class.
 */
data class BeaconTextContentDto(
    val id: Long,
    val language: String,
    val description: String,
    @SerializedName("created_at") val createdAt: Date,
    @SerializedName("updated_at")val updatedAt: Date
)
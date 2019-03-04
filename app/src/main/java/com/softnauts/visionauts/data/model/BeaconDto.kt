package com.softnauts.visionauts.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Beacon data transfer object class.
 */
data class BeaconDto(
    val id: Long,
    val uuid: String,
    val minor: String,
    val major: String,
    val enabled: Boolean,
    val texts: List<BeaconTextContentDto>,
    @SerializedName("created_at") val createdAt: Date,
    @SerializedName("updated_at") val updatedAt: Date
)
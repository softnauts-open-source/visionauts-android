package com.softnauts.visionauts.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.softnauts.visionauts.data.entity.BeaconEntity
import com.softnauts.visionauts.data.entity.BeaconTextContentEntity

/**
 * Helper class used to get Beacon with its content from local database (Room).
 */
class BeaconWithTexts {
    @Embedded
    var beaconEntity: BeaconEntity? = null

    @Relation(parentColumn = "id", entityColumn = "beaconId", entity = BeaconTextContentEntity::class)
    var textEntities: List<BeaconTextContentEntity>? = null
}
package com.softnauts.visionauts.events

import com.softnauts.visionauts.data.model.SimpleBeaconModel

/**
 * Event triggered when new beacon is found.
 */
data class BeaconFoundEvent(val simpleBeaconModel: SimpleBeaconModel)
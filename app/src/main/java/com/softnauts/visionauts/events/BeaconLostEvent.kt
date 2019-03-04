package com.softnauts.visionauts.events

import com.softnauts.visionauts.data.model.SimpleBeaconModel

/**
 * Event triggered when beacon is lost.
 */
data class BeaconLostEvent(val simpleBeaconModel: SimpleBeaconModel)
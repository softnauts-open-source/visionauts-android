package com.softnauts.visionauts.common

import com.softnauts.visionauts.data.model.SimpleBeaconModel
import com.softnauts.visionauts.events.BeaconFoundEvent
import com.softnauts.visionauts.events.BeaconLostEvent
import org.greenrobot.eventbus.EventBus

/**
 * Class used to emits event bus events.
 */
class BeaconsStateEventEmitter {
    /**
     * Emits beacon found event.
     */
    fun emmitBeaconFoundEvent(simpleBeaconModel: SimpleBeaconModel){
        EventBus.getDefault().post(BeaconFoundEvent(simpleBeaconModel))
    }

    /**
     * Emits beacon lost event.
     */
    fun emmitBeaconLostEvent(simpleBeaconModel: SimpleBeaconModel){
        EventBus.getDefault().post(BeaconLostEvent(simpleBeaconModel))
    }
}
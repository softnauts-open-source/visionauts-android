package com.softnauts.visionauts.data.model

/**
 * Main activity view state.
 *
 * DETECTING - when app is searching for beacons.
 * BLUETOOTH - when bluetooth is disabled and app is trying to turn it on.
 * BEACONS - when there are some beacons in range.
 */
enum class MainActivityViewState {
    DETECTING, BLUETOOTH, BEACONS
}
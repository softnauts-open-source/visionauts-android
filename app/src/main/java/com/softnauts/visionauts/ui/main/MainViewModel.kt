package com.softnauts.visionauts.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softnauts.visionauts.common.BluetoothHelper
import com.softnauts.visionauts.common.Event
import com.softnauts.visionauts.data.datasource.BeaconDataSource
import com.softnauts.visionauts.data.model.BeaconDto
import com.softnauts.visionauts.data.model.MainActivityViewState
import com.softnauts.visionauts.data.model.SimpleBeaconModel
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val beaconDataSource: BeaconDataSource,
    private val bluetoothHelper: BluetoothHelper
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val beaconsList = mutableListOf<BeaconDto>()
    val currentItemPosition = MutableLiveData<Int>().apply { value = NONE }
    val currentItemLiveData = MutableLiveData<Event<BeaconDto>>()

    val viewState = MutableLiveData<MainActivityViewState>().apply {
        value = MainActivityViewState.BLUETOOTH
    }

    /**
     * Method called when activity is resumed.
     */
    fun onActivityResumed() {
        if (bluetoothHelper.isEnabled()) {
            onBluetoothOn()
        } else {
            bluetoothHelper.turnOnBluetooth()
        }
    }

    /**
     * Method called when bluetooth is turned on.
     */
    fun onBluetoothOn(list : List<BeaconDto> = beaconsList) {
        if (list.isNotEmpty()) {
            viewState.value = MainActivityViewState.BEACONS
        } else {
            viewState.value = MainActivityViewState.DETECTING
        }
    }

    /**
     * Method called when bluetooth is turned off. Method tries to auto turn bluetooth on.
     */
    fun onBluetoothOff() {
        viewState.value = MainActivityViewState.BLUETOOTH
        bluetoothHelper.turnOnBluetooth()
    }

    /**
     * Method called when beacon is found. Method looks for beacon with specified uuid, minor and major in local database, then if found
     * adds it to list and updates UI.
     */
    fun onBeaconFound(simpleBeaconModel: SimpleBeaconModel) {
        simpleBeaconModel.apply {
            compositeDisposable.add(
                beaconDataSource.getBeaconByIdentifier(uuid, minor, major)
                    .subscribe({ it ->
                        if(beaconsList.contains(it).not()){
                            beaconsList.add(0, it)
                            currentItemPosition.value = 0
                            currentItemLiveData.value = Event(it)
                            viewState.value = MainActivityViewState.BEACONS
                        }
                    }, { t ->
                        Timber.e(t)
                    })
            )
        }
    }
    /**
     * Method called when beacon is lost. Method looks for beacon with specified uuid, minor and major in local database, then if found
     * removes it from list and updates UI.
     */
    fun onBeaconLost(simpleBeaconModel: SimpleBeaconModel) {
        simpleBeaconModel.apply {
            compositeDisposable.add(
                beaconDataSource.getBeaconByIdentifier(uuid, minor, major)
                    .subscribe({ it ->
                        beaconsList.remove(it)
                        if(beaconsList.isEmpty()){
                            currentItemPosition.value = NONE
                            viewState.value = MainActivityViewState.DETECTING
                        }else{
                            beaconsList[currentItemPosition.value!!].apply {
                                if(this == it){
                                    currentItemPosition.value = 0
                                    currentItemLiveData.value = Event(beaconsList.first())
                                }else{
                                    currentItemPosition.value = beaconsList.indexOf(this)
                                    currentItemLiveData.value = Event(this)
                                }
                            }
                        }
                    }, { t ->
                        Timber.e(t)
                    })
            )
        }
    }

    /**
     * Method used to get beacons in range count.
     */
    fun itemsCount() = beaconsList.size

    /**
     * Method used to display previous beacon.
     */
    fun onPreviousClicked(){
        if(currentItemPosition.value!! > 0){
            currentItemPosition.value = currentItemPosition.value!! - 1
            currentItemLiveData.value = Event(beaconsList[currentItemPosition.value!!])
        }
    }

    /**
     * Method used to display next beacon.
     */
    fun onNextClicked(){
        if(currentItemPosition.value!! < beaconsList.size - 1){
            currentItemPosition.value = currentItemPosition.value!! + 1
            currentItemLiveData.value = Event(beaconsList[currentItemPosition.value!!])
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    companion object {
        const val NONE = -1
    }

}
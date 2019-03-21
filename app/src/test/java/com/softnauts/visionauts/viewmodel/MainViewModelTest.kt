package com.softnauts.visionauts.viewmodel

import com.softnauts.visionauts.rule.TrampolineSchedulerRule
import com.softnauts.visionauts.common.BluetoothHelper
import com.softnauts.visionauts.data.datasource.BeaconDataSource
import com.softnauts.visionauts.data.model.BeaconDto
import com.softnauts.visionauts.data.model.MainActivityViewState
import com.softnauts.visionauts.data.model.SimpleBeaconModel
import com.softnauts.visionauts.ui.main.MainViewModel
import io.reactivex.Maybe
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MainViewModelTest {

    @get:Rule
    var trampolineSchedulerRule = TrampolineSchedulerRule()

    private lateinit var mainViewModel: MainViewModel

    @Mock
    lateinit var beaconDataSource: BeaconDataSource

    @Mock
    lateinit var bluetoothHelper: BluetoothHelper

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        mainViewModel = MainViewModel(beaconDataSource, bluetoothHelper)
    }

    @Test
    fun turnOnBluetoothOnResumePositiveTest(){
        Mockito.`when`(bluetoothHelper.isEnabled()).thenReturn(false)

        mainViewModel.onActivityResumed()

        Mockito.verify(bluetoothHelper, times(1)).turnOnBluetooth()
    }

    @Test
    fun turnOnBluetoothOnResumeNegativeTest(){
        Mockito.`when`(bluetoothHelper.isEnabled()).thenReturn(true)

        mainViewModel.onActivityResumed()

        Mockito.verify(bluetoothHelper, never()).turnOnBluetooth()
    }

    @Test
    fun onBluetoothOnEmptyBeaconsListTest(){
        mainViewModel.onBluetoothOn(emptyList())

        assertEquals(mainViewModel.viewState.value, MainActivityViewState.DETECTING)
    }

    @Test
    fun onBluetoothOnNotEmptyBeaconsListTest(){
        mainViewModel.onBluetoothOn(mutableListOf(BeaconDto(1L, "", "", "", true, emptyList(), Date(), Date())))

        assertEquals(mainViewModel.viewState.value, MainActivityViewState.BEACONS)
    }

    @Test
    fun onBluetoothOffTest(){
        mainViewModel.onBluetoothOff()

        assertEquals(mainViewModel.viewState.value, MainActivityViewState.BLUETOOTH)
        Mockito.verify(bluetoothHelper, times(1)).turnOnBluetooth()
    }

    @Test
    fun onBeaconFoundPositiveTest(){
        val beaconDto = BeaconDto(1L, "uuid", "minor", "major", true, emptyList(), Date(), Date())
        Mockito
            .`when`(beaconDataSource.getBeaconByIdentifier(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .thenReturn(Maybe.just(beaconDto))

        mainViewModel.onBeaconFound(SimpleBeaconModel("uuid", "minor", "major"))

        assertTrue(mainViewModel.beaconsList.contains(beaconDto))
        assertEquals(MainActivityViewState.BEACONS, mainViewModel.viewState.value)
    }

    @Test
    fun onBeaconFoundNegativeTest(){
        val beaconDto = BeaconDto(1L, "uuid", "minor", "major", true, emptyList(), Date(), Date())
        Mockito
            .`when`(beaconDataSource.getBeaconByIdentifier(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .thenReturn(Maybe.empty())

        mainViewModel.onBeaconFound(SimpleBeaconModel("uuid", "minor", "major"))

        assertTrue(mainViewModel.beaconsList.contains(beaconDto).not())
        assertNotSame(MainActivityViewState.BEACONS, mainViewModel.viewState.value)
    }

    @Test
    fun onBeaconLostPositiveTest(){
        val beaconDto = BeaconDto(1L, "uuid", "minor", "major", true, emptyList(), Date(), Date())
        Mockito
            .`when`(beaconDataSource.getBeaconByIdentifier(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .thenReturn(Maybe.just(beaconDto))

        mainViewModel.beaconsList.add(beaconDto)
        mainViewModel.currentItemPosition.value = 0
        assertTrue(mainViewModel.beaconsList.contains(beaconDto))
        mainViewModel.onBeaconLost(SimpleBeaconModel("uuid", "minor", "major"))
        assertTrue(mainViewModel.beaconsList.contains(beaconDto).not())
    }

    @Test
    fun onBeaconLostNegativeTest(){
        val beaconDto = BeaconDto(1L, "uuid", "minor", "major", true, emptyList(), Date(), Date())
        Mockito
            .`when`(beaconDataSource.getBeaconByIdentifier(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .thenReturn(Maybe.empty())

        mainViewModel.beaconsList.add(beaconDto)
        assertTrue(mainViewModel.beaconsList.contains(beaconDto))
        mainViewModel.onBeaconLost(SimpleBeaconModel("uuid", "minor", "major"))
        assertTrue(mainViewModel.beaconsList.contains(beaconDto))
    }

}
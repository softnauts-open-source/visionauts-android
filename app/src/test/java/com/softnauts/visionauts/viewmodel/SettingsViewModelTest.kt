package com.softnauts.visionauts.viewmodel

import com.softnauts.visionauts.rule.TrampolineSchedulerRule
import com.softnauts.visionauts.data.datasource.SettingsDataSource
import com.softnauts.visionauts.ui.settings.SettingsViewModel
import io.reactivex.Observable
import io.reactivex.Single
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.Exception

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SettingsViewModelTest {

    @get:Rule
    var trampolineSchedulerRule = TrampolineSchedulerRule()

    @Mock
    lateinit var settingsDataSource: SettingsDataSource

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun getRangePositiveTest(){
        Mockito.`when`(settingsDataSource.getRange()).thenReturn(Observable.just(1.0f))
        viewModel = SettingsViewModel(settingsDataSource)

        assertEquals(viewModel.rangeLiveData.value, 1.0f)
        assertEquals(viewModel.rangeSingleEvent.value?.peekContent(), 1.0f)
    }

    @Test
    fun getRangeNegativeTest(){
        Mockito.`when`(settingsDataSource.getRange()).thenReturn(Observable.error(Exception("exception")))
        viewModel = SettingsViewModel(settingsDataSource)

        assertEquals(viewModel.rangeLiveData.value, null)
        assertEquals(viewModel.rangeSingleEvent.value?.peekContent(), null)
    }

    @Test
    fun saveRangePositiveTest(){
        val newRange = 1.0f
        val oldRange = 0.5f

        Mockito.`when`(settingsDataSource.getRange()).thenReturn(Observable.just(oldRange))
        viewModel = SettingsViewModel(settingsDataSource)

        Mockito.`when`(settingsDataSource.saveRange(Mockito.anyFloat())).thenReturn(Single.just(newRange))

        viewModel.saveRange(newRange)

        assertEquals(viewModel.rangeLiveData.value, newRange)
    }

    @Test
    fun saveRangeNegativeTest(){
        val newRange = 1.0f
        val oldRange = 0.5f

        Mockito.`when`(settingsDataSource.getRange()).thenReturn(Observable.just(oldRange))
        viewModel = SettingsViewModel(settingsDataSource)

        Mockito.`when`(settingsDataSource.saveRange(Mockito.anyFloat())).thenReturn(Single.error(Exception("exception")))

        viewModel.saveRange(newRange)

        assertEquals(viewModel.rangeLiveData.value, oldRange)
    }
}
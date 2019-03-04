package com.softnauts.visionauts.repository

import android.content.SharedPreferences
import com.softnauts.visionauts.data.repository.SettingsRepository
import com.softnauts.visionauts.data.repository.SettingsRepository.Companion.SETTINGS_RANGE_KEY
import com.softnauts.visionauts.rule.TrampolineSchedulerRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SettingsRepositoryTest {

    @get:Rule
    var trampolineSchedulerRule = TrampolineSchedulerRule()

    @Mock
    lateinit var sharedPreferences: SharedPreferences

    @Mock
    lateinit var editor: SharedPreferences.Editor

    private lateinit var settingsRepository: SettingsRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        settingsRepository = SettingsRepository(sharedPreferences)
    }

    @Test
    fun getRangeTest() {
        val range = 2.5f
        Mockito.`when`(sharedPreferences.getFloat(anyString(), anyFloat())).thenReturn(range)

        val observer = settingsRepository.getRange().test()
        observer.assertValue(range)
        observer.assertComplete()
    }

    @Test
    fun saveRangeTest() {
        val range = 2.5f

        Mockito.`when`(sharedPreferences.edit()).thenReturn(editor)
        Mockito.`when`(editor.putFloat(anyString(), anyFloat())).thenReturn(editor)

        val observer = settingsRepository.saveRange(range).test()
        observer.assertValue(range)
        observer.assertComplete()

        Mockito.verify(editor, times(1)).putFloat(SETTINGS_RANGE_KEY, range)
        Mockito.verify(editor, times(1)).commit()
    }
}
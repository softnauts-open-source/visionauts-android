package com.softnauts.visionauts.helper

import com.softnauts.visionauts.common.BeaconContentHelper
import com.softnauts.visionauts.common.LocaleManager
import com.softnauts.visionauts.data.model.BeaconTextContentDto
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*

class BeaconContentHelperTest {

    @Mock
    lateinit var localeManager: LocaleManager

    private lateinit var beaconContentHelper: BeaconContentHelper

    private var emptyTextList = emptyList<BeaconTextContentDto>()
    private var englishTextsList = mutableListOf(BeaconTextContentDto(1L, "en", "english", Date(), Date()))
    private var nonEglishList = mutableListOf(BeaconTextContentDto(1L, "de", "german", Date(), Date()))
    private var englishPolishList  = mutableListOf(BeaconTextContentDto(1L, "en", "english", Date(), Date()),
        BeaconTextContentDto(2L, "pl", "polish", Date(), Date()))

    private var englishPolishGermanList  = mutableListOf(BeaconTextContentDto(1L, "en", "english", Date(), Date()),
        BeaconTextContentDto(2L, "pl", "polish", Date(), Date()),
        BeaconTextContentDto(3L, "de", "german", Date(), Date()))


    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        beaconContentHelper = BeaconContentHelper(localeManager)
    }

    @Test
    fun emptyContentListTest() {
        Mockito.`when`(localeManager.getLocale()).thenReturn(Locale.ENGLISH)
        val result = beaconContentHelper.getText(emptyTextList)
        assertEquals("", result)
    }

    @Test
    fun englishLocaleEnglishContentTest() {
        Mockito.`when`(localeManager.getLocale()).thenReturn(Locale.ENGLISH)
        val result = beaconContentHelper.getText(englishTextsList)
        assertEquals("english", result)
    }

    @Test
    fun englishLocaleNonEnglishContentTest() {
        Mockito.`when`(localeManager.getLocale()).thenReturn(Locale.ENGLISH)
        val result = beaconContentHelper.getText(nonEglishList)
        assertEquals("german", result)
    }

    @Test
    fun englishLocaleMixedContentTest() {
        Mockito.`when`(localeManager.getLocale()).thenReturn(Locale.ENGLISH)
        val result = beaconContentHelper.getText(englishPolishList)
        assertEquals("english", result)
    }

    @Test
    fun germanLocaleEnglishContentTest() {
        Mockito.`when`(localeManager.getLocale()).thenReturn(Locale.GERMAN)
        val result = beaconContentHelper.getText(englishTextsList)
        assertEquals("english", result)
    }

    @Test
    fun germanLocaleGermanContentTest() {
        Mockito.`when`(localeManager.getLocale()).thenReturn(Locale.GERMAN)
        val result = beaconContentHelper.getText(nonEglishList)
        assertEquals("german", result)
    }

    @Test
    fun germanLocaleNonGermanContentTest() {
        Mockito.`when`(localeManager.getLocale()).thenReturn(Locale.GERMAN)
        val result = beaconContentHelper.getText(englishPolishList)
        assertEquals("english", result)
    }

    @Test
    fun germanLocaleMixedContentTest() {
        Mockito.`when`(localeManager.getLocale()).thenReturn(Locale.GERMAN)
        val result = beaconContentHelper.getText(englishPolishGermanList)
        assertEquals("german", result)
    }
}

package com.softnauts.visionauts.presenter

import com.softnauts.visionauts.rule.TrampolineSchedulerRule
import com.softnauts.visionauts.common.DownloadDataException
import com.softnauts.visionauts.data.datasource.BeaconDataSource
import com.softnauts.visionauts.ui.init.InitPresenter
import com.softnauts.visionauts.ui.init.InitRouter
import com.softnauts.visionauts.ui.init.InitView
import io.reactivex.Completable
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
import java.lang.Exception

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class InitPresenterTest {

    @get:Rule
    var trampolineSchedulerRule = TrampolineSchedulerRule()

    @Mock
    lateinit var beaconDataSource: BeaconDataSource

    @Mock
    lateinit var initView: InitView

    @Mock
    lateinit var initRouter: InitRouter

    private lateinit var presenter: InitPresenter

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        presenter = InitPresenter(beaconDataSource, initView, initRouter)
    }

    @Test
    fun onActivityCreateTest(){
        Mockito.`when`(beaconDataSource.getBeaconsFromApiIfNeeded()).thenReturn(Completable.complete())
        presenter.onActivityCreate()

        Mockito.verify(beaconDataSource, times(1)).getBeaconsFromApiIfNeeded()
    }

    @Test
    fun getBeaconsPositiveTest(){
        Mockito.`when`(beaconDataSource.getBeaconsFromApiIfNeeded()).thenReturn(Completable.complete())
        presenter.onActivityCreate()

        Mockito.verify(initView, times(1)).showLoader()
        Mockito.verify(initView, times(1)).hideLoader()
        Mockito.verify(initRouter, times(1)).navigateToMainActivity()

        Mockito.verify(initView, never()).showErrorMessage(ArgumentMatchers.anyString())
        Mockito.verify(initView, never()).showFirstRunDownloadDataError()
    }

    @Test
    fun getBeaconsDownloadDataExceptionNegativeTest(){
        Mockito.`when`(beaconDataSource.getBeaconsFromApiIfNeeded()).thenReturn(Completable.error(DownloadDataException("downloadDataException")))
        presenter.onActivityCreate()

        Mockito.verify(initView, times(1)).showLoader()
        Mockito.verify(initView, times(1)).hideLoader()
        Mockito.verify(initRouter, never()).navigateToMainActivity()

        Mockito.verify(initView, never()).showErrorMessage("downloadDataException")
        Mockito.verify(initView, times(1)).showFirstRunDownloadDataError()
    }

    @Test
    fun getBeaconsUnknownExceptionNegativeTest(){
        Mockito.`when`(beaconDataSource.getBeaconsFromApiIfNeeded()).thenReturn(Completable.error(Exception("exception")))
        presenter.onActivityCreate()

        Mockito.verify(initView, times(1)).showLoader()
        Mockito.verify(initView, times(1)).hideLoader()
        Mockito.verify(initRouter, never()).navigateToMainActivity()

        Mockito.verify(initView, times(1)).showErrorMessage("exception")
        Mockito.verify(initView, never()).showFirstRunDownloadDataError()
    }
}
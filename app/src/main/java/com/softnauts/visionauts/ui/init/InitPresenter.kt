package com.softnauts.visionauts.ui.init

import com.softnauts.visionauts.common.DownloadDataException
import com.softnauts.visionauts.data.datasource.BeaconDataSource
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

/**
 * InitActivity presenter class.
 */
class InitPresenter @Inject constructor(
    private val beaconDataSource: BeaconDataSource,
    private val view: InitView,
    private val router: InitRouter
) {
    private val compositeDisposable = CompositeDisposable()

    /**
     * Method called when activity is created.
     */
    fun onActivityCreate() {
        downloadBeaconData()
    }

    /**
     * Method used to download beacons data.
     */
    fun downloadBeaconData() {
        view.showLoader()
        compositeDisposable.add(
            beaconDataSource.getBeaconsFromApiIfNeeded().subscribe({
                view.hideLoader()
                router.navigateToMainActivity()
            }, { t ->
                view.hideLoader()
                Timber.e(t)
                if(t is DownloadDataException){
                    view.showFirstRunDownloadDataError()
                }else{
                    view.showErrorMessage(t.localizedMessage)
                }
            })
        )
    }

    /**
     * Method called when activity is destroyed.
     */
    fun onActivityDestroy() {
        compositeDisposable.dispose()
    }
}
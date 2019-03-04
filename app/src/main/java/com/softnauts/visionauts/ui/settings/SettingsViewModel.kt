package com.softnauts.visionauts.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softnauts.visionauts.common.Event
import com.softnauts.visionauts.data.datasource.SettingsDataSource
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private val settingsDataSource: SettingsDataSource) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    val rangeLiveData = MutableLiveData<Float>()
    val rangeSingleEvent = MutableLiveData<Event<Float>>()

    /**
     * Method called when SettingsViewModel is initialized.
     */
    init {
       getRange()
    }

    /**
     * Method downloads actual range setting from repository and sets value to livedata.
     */
    fun getRange(){
        compositeDisposable.add(
            settingsDataSource.getRange().subscribe(
                { it ->
                    rangeLiveData.value = it
                    rangeSingleEvent.value = Event(it)
                }, { t ->
                    Timber.e(t)
                }
            )
        )
    }

    /**
     * Method saves new range setting to repository.
     */
    fun saveRange(range: Float) {
        compositeDisposable.add(
            settingsDataSource.saveRange(range).subscribe(
                { it ->
                    rangeLiveData.value = it
                }, { t ->
                    Timber.e(t)
                }
            )
        )
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
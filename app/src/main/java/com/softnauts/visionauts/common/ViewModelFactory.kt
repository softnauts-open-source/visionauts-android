package com.softnauts.visionauts.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.softnauts.visionauts.di.scope.ApplicationScope
import javax.inject.Inject
import javax.inject.Provider

/**
 * Factory class used to deliver view models.
 */
@ApplicationScope
class ViewModelFactory @Inject constructor(
        private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) :
        ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]
        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }
        if (creator == null) {
            throw IllegalArgumentException("unknown model class $modelClass")
        }
        try {
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}
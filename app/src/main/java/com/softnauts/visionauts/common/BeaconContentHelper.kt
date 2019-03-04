package com.softnauts.visionauts.common

import com.softnauts.visionauts.data.model.BeaconTextContentDto
import javax.inject.Inject

class BeaconContentHelper @Inject constructor(private val localeManager: LocaleManager) {
    fun getText(texts: List<BeaconTextContentDto>): String{
        val currentLanguage = localeManager.getLocale().language

        texts.find { it.language == currentLanguage }.apply {
            return try {
                when(this){
                    null -> texts.find { it.language == "en" }?.description?: texts.first().description
                    else -> this.description
                }
            } catch (e: Exception){
                ""
            }
        }
    }

}
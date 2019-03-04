package com.softnauts.visionauts.common

import java.util.*
import javax.inject.Inject

class LocaleManagerImpl @Inject constructor() : LocaleManager{
    override fun getLocale(): Locale = Locale.getDefault()
}
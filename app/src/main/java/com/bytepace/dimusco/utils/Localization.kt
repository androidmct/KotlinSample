package com.bytepace.dimusco.utils

import android.content.Context
import com.bytepace.dimusco.service.LocalizationService

fun getTranslatedString(context: Context, key: String): String {
    return LocalizationService.instance.getString(key) ?: getStringByName(context, key)
}
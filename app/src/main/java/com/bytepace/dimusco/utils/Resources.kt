package com.bytepace.dimusco.utils

import android.content.Context

private const val RES_STRING = "string"

fun getStringByName(context: Context, name: String): String {
    return when (val id = context.resources?.getIdentifier(name, RES_STRING, context.packageName)) {
        null -> ""
        else -> context.resources?.getString(id) ?: ""
    }
}
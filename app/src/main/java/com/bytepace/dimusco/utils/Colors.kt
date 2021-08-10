package com.bytepace.dimusco.utils

import androidx.core.graphics.ColorUtils

fun getOpaqueColor(color: Int): Int {
    return ColorUtils.setAlphaComponent(color, 0xFF)
}

fun getColorWithOpacity(color: Int, opacity: Float): Int {
    return ColorUtils.setAlphaComponent(color, (opacity * 255).toInt())
}

fun getHexString(color: Int): String {
    return String.format("#%06X", (0xFFFFFF and color))
}
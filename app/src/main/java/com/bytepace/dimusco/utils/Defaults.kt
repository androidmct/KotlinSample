package com.bytepace.dimusco.utils

import com.bytepace.dimusco.data.model.Color
import com.bytepace.dimusco.data.model.Symbol

const val DEFAULT_SCROLLING_HORIZONTAL = true
const val DEFAULT_CONFIRM_SAVING_CHANGES = true
const val DEFAULT_TRANSPARENCY = 1f
const val DEFAULT_LINE_THICKNESS = 0.05f
const val DEFAULT_ERASER_THICKNESS = 1.0f
const val DEFAULT_TEXT_SIZE = 0.75f
const val DEFAULT_SELECTED_COLOR = 0xFF00FF
const val DEFAULT_IMAGE_BASED_DRAWINGS = true
const val DEFAULT_IMAGE_BASED_SYMBOLS = true
const val DEFAULT_IMAGE_BASED_TEXT = true
const val DEFAULT_EDIT_TIMEOUT = 2000L
val DEFAULT_COLORS: List<Color>
    get() = getDefaultColors()
val DEFAULT_SYMBOLS: List<Symbol>
    get() = getDefaultSymbols()

private fun getDefaultColors(): List<Color> {
    return GLOBAL_COLORS.map { Color(it) }
}

private fun getDefaultSymbols(): List<Symbol> {
    return mutableListOf<Symbol>().apply {
        // 74 corresponds amount of files in assets/symbols
        for (index in 0..74) {
            this.add(Symbol(index, index, 1f))
        }
    }
}
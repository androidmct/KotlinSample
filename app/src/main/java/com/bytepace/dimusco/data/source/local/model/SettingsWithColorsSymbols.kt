package com.bytepace.dimusco.data.source.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class SettingsWithColorsSymbols(

    @Embedded
    val settings: SettingsDB,

    @Relation(parentColumn = "userId", entityColumn = "userId")
    val colors: List<ColorDB>,

    @Relation(parentColumn = "userId", entityColumn = "userId")
    val symbols: List<SymbolDB>
)
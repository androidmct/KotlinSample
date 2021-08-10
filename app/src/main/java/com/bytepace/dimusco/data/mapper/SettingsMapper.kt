package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.model.Settings
import com.bytepace.dimusco.data.source.local.model.SettingsWithColorsSymbols

object SettingsMapper {

    fun toLocalSettings(userId: String, settings: Settings
    ): com.bytepace.dimusco.data.source.local.model.SettingsDB {
        return com.bytepace.dimusco.data.source.local.model.SettingsDB(
            userId,
            settings.pageScrollingHorizontal,
            settings.confirmSavingChanges,
            settings.transparency,
            settings.lineThickness,
            settings.eraserThickness,
            settings.textSize,
            settings.selectedColor,
            settings.isImageDrawings,
            settings.isImageSymbols,
            settings.isImageText,
            settings.editTimeout
        )
    }

    fun fromLocalSettings(settings: SettingsWithColorsSymbols): Settings {
        return Settings(
            settings.settings.pageScrollingHorizontal,
            settings.settings.confirmSavingChanges,
            settings.settings.transparency,
            settings.settings.lineThickness,
            settings.settings.eraserThickness,
            settings.settings.textSize,
            settings.settings.selectedColor,
            settings.settings.isImageDrawings,
            settings.settings.isImageSymbols,
            settings.settings.isImageText,
            settings.settings.editTimeout,
            settings.colors.map { ColorMapper.fromLocalColor(it) },
            settings.symbols.map { SymbolMapper.fromLocalSymbol(it) }
        )
    }
}
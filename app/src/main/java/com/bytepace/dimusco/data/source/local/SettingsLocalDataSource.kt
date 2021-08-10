package com.bytepace.dimusco.data.source.local

import androidx.lifecycle.LiveData
import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import com.bytepace.dimusco.data.source.local.model.ColorDB
import com.bytepace.dimusco.data.source.local.model.SettingsDB
import com.bytepace.dimusco.data.source.local.model.SettingsWithColorsSymbols
import com.bytepace.dimusco.data.source.local.model.SymbolDB

class SettingsLocalDataSource {

    private val database: DimuscoDatabase by lazy {
        DimuscoDatabase.getInstance()
    }

    fun getSettings(userId: String): LiveData<SettingsWithColorsSymbols> {
        return database.settingsDao().select(userId)
    }

    fun getSettingsSync(userId: String): SettingsWithColorsSymbols {
        return database.settingsDao().selectSync(userId)
    }

    fun updateColor(color: ColorDB) {
        database.colorDao().update(color)
    }

    fun updateColors(userId: String, colors: List<ColorDB>) {
        database.runInTransaction {
            database.colorDao().deleteRedundant(userId, colors.map { it.order })
            database.colorDao().insert(colors)
        }
    }

    fun updateSymbol(symbol: SymbolDB) {
        database.symbolDao().update(symbol)
    }

    fun updateSymbols(userId: String, symbols: List<SymbolDB>) {
        database.runInTransaction {
            database.symbolDao().deleteRedundant(userId, symbols.map { it.order })
            database.symbolDao().insert(symbols)
        }
    }

    fun updateSettings(settings: SettingsDB) {
        database.settingsDao().update(settings)
    }

    fun updateScrollDirection(userId: String, isHorizontal: Boolean) {
        database.settingsDao().updateScrollDirection(userId, isHorizontal)
    }

    fun updateConfirmSavingChanges(userId: String, confirm: Boolean) {
        database.settingsDao().updateConfirmSavingChanges(userId, confirm)
    }

    fun updateTransparency(userId: String, transparency: Float) {
        database.settingsDao().updateTransparency(userId, transparency)
    }

    fun updateLineThickness(userId: String, thickness: Float) {
        database.settingsDao().updateLineThickness(userId, thickness)
    }

    fun updateEraserThickness(userId: String, thickness: Float) {
        database.settingsDao().updateEraserThickness(userId, thickness)
    }

    fun updateTextSize(userId: String, textSize: Float) {
        database.settingsDao().updateTextSize(userId, textSize)
    }

    fun updateSelectedColor(userId: String, color: Int) {
        database.settingsDao().updateSelectedColor(userId, color)
    }

    fun updateIsImageDrawings(userId: String, isImage: Boolean) {
        database.settingsDao().updateIsImageDrawings(userId, isImage)
    }

    fun updateIsImageSymbols(userId: String, isImage: Boolean) {
        database.settingsDao().updateIsImageSymbols(userId, isImage)
    }

    fun updateIsImageText(userId: String, isImage: Boolean) {
        database.settingsDao().updateIsImageText(userId, isImage)
    }

    fun updateEditTimeout(userId: String, milliseconds: Long) {
        database.settingsDao().updateEditTimeout(userId, milliseconds)
    }
}
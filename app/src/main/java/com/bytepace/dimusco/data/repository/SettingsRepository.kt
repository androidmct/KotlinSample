package com.bytepace.dimusco.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.bytepace.dimusco.data.mapper.ColorMapper
import com.bytepace.dimusco.data.mapper.SettingsMapper
import com.bytepace.dimusco.data.mapper.SymbolMapper
import com.bytepace.dimusco.data.model.Color
import com.bytepace.dimusco.data.model.Settings
import com.bytepace.dimusco.data.model.Symbol
import com.bytepace.dimusco.data.source.local.SettingsLocalDataSource
import com.bytepace.dimusco.data.source.remote.socket.SocketService
import com.bytepace.dimusco.data.source.remote.socket.SocketSettingsListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class SettingsRepository private constructor(
    private val userId: String
) : SocketSettingsListener {

    companion object {
        private lateinit var instance: SettingsRepository

        fun init(userId: String) {
            instance = SettingsRepository(userId)
        }

        fun get(): SettingsRepository {
            if (::instance.isInitialized) {
                return instance
            }
            throw UninitializedPropertyAccessException(
                "Call init(String) before using this method."
            )
        }
    }

    val settings: LiveData<Settings>
        get() = getTransformedSettings()

    private val local = SettingsLocalDataSource()

    init {
        SocketService.setSettingsCallback(this)
    }

    suspend fun getSettings(): Settings {
        return withContext(coroutineContext + Dispatchers.IO) {
            getTransformedSettingsSync()
        }
    }

    suspend fun updateScrollDirection(isHorizontal: Boolean) {
        runAsync { local.updateScrollDirection(userId, isHorizontal) }
    }

    suspend fun updateConfirmSavingChanges(confirm: Boolean) {
        runAsync { local.updateConfirmSavingChanges(userId, confirm) }
    }

    suspend fun updateTransparency(transparency: Float) {
        runAsync { local.updateTransparency(userId, transparency) }
    }

    suspend fun updateLineThickness(thickness: Float) {
        runAsync { local.updateLineThickness(userId, thickness) }
    }

    suspend fun updateEraserThickness(thickness: Float) {
        runAsync { local.updateEraserThickness(userId, thickness) }
    }

    suspend fun updateTextSize(textSize: Float) {
        runAsync { local.updateTextSize(userId, textSize) }
    }

    suspend fun updateSelectedColor(color: Int) {
        runAsync { local.updateSelectedColor(userId, color) }
    }

    suspend fun updateIsImageDrawings(isImage: Boolean) {
        runAsync { local.updateIsImageDrawings(userId, isImage) }
    }

    suspend fun updateIsImageSymbols(isImage: Boolean) {
        runAsync { local.updateIsImageSymbols(userId, isImage) }
    }

    suspend fun updateIsImageText(isImage: Boolean) {
        runAsync { local.updateIsImageText(userId, isImage) }
    }

    suspend fun updateEditTimeout(milliseconds: Long) {
        runAsync { local.updateEditTimeout(userId, milliseconds) }
    }

    suspend fun updateColor(order: Int, color: Color) {
        runAsync { local.updateColor(ColorMapper.toLocalColor(userId, order, color)) }
    }

    suspend fun updateColors(colors: List<Color>) {
        val localColors = colors.mapIndexed { index, color ->
            ColorMapper.toLocalColor(userId, index, color)
        }
        runAsync { local.updateColors(userId, localColors) }
    }

    suspend fun updateSymbol(symbol: Symbol) {
        runAsync { local.updateSymbol(SymbolMapper.toLocalSymbol(userId, symbol)) }
    }

    suspend fun updateSymbols(symbols: List<Symbol>) {
        runAsync {
            local.updateSymbols(userId, symbols.map { SymbolMapper.toLocalSymbol(userId, it) })
        }
    }

    private fun getTransformedSettings(): LiveData<Settings> {

        return Transformations.map(local.getSettings(userId)){


            SettingsMapper.fromLocalSettings(it)
        }
    }

    private fun getTransformedSettingsSync(): Settings {
        Log.d("useridd-withoutlivedata",userId.toString())
        return SettingsMapper.fromLocalSettings(local.getSettingsSync(userId))
    }

    private suspend fun runAsync(function: () -> Unit) {
        return withContext(coroutineContext + Dispatchers.IO) {
            function()
        }
    }

    override fun onGetSettings(settings: Settings) {
        local.updateSettings(SettingsMapper.toLocalSettings(userId, settings))
        local.updateColors(userId, settings.colors.mapIndexed { index, color ->
            ColorMapper.toLocalColor(userId, index, color)
        })
        local.updateSymbols(userId, settings.symbols.map { SymbolMapper.toLocalSymbol(userId, it) })
    }
}
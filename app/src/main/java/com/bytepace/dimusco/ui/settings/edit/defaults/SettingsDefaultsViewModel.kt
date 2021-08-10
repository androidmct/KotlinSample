package com.bytepace.dimusco.ui.settings.edit.defaults

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytepace.dimusco.data.model.Settings
import com.bytepace.dimusco.data.repository.SettingsRepository
//import com.bytepace.dimusco.navigation.SettingsNavigator
import kotlinx.coroutines.launch

class SettingsDefaultsViewModel : ViewModel() {

    val settings: LiveData<Settings>

    private val settingsRepository = SettingsRepository.get()

    init {
        settings = settingsRepository.settings
    }

    fun onClickBack() {
//        SettingsNavigator.get().back()
    }

    fun onTransparencyChanged(value: Float) {
        viewModelScope.launch { settingsRepository.updateTransparency(value) }
    }

    fun onLineThicknessChanged(value: Float) {
        viewModelScope.launch { settingsRepository.updateLineThickness(value) }
    }

    fun onEraserThicknessChanged(value: Float) {
        viewModelScope.launch { settingsRepository.updateEraserThickness(value) }
    }

    fun onTextSizeChanged(value: Float) {
        viewModelScope.launch { settingsRepository.updateTextSize(value) }
    }
}
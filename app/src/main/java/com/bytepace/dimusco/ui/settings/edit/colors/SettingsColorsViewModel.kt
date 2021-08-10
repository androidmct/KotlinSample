package com.bytepace.dimusco.ui.settings.edit.colors

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytepace.dimusco.data.model.Color
import com.bytepace.dimusco.data.model.Settings
import com.bytepace.dimusco.data.repository.SettingsRepository
//import com.bytepace.dimusco.navigation.SettingsNavigator
import com.bytepace.dimusco.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class SettingsColorsViewModel : ViewModel() {

    val settings: LiveData<Settings>

    val onSetColor: LiveData<Any>
        get() = onSetColorEvent
    private val onSetColorEvent = SingleLiveEvent<Any>()

    private val settingsRepository = SettingsRepository.get()

    init {
        settings = settingsRepository.settings
    }

    fun onClickBack() {
//        SettingsNavigator.get().back()
    }

    fun onClickSetColor() {
        onSetColorEvent.call()
    }

    fun updateSelectedColor(color: Color) {
        viewModelScope.launch { settingsRepository.updateSelectedColor(color.value) }
    }

    fun updateColor(order: Int, color: Color) {
        viewModelScope.launch { settingsRepository.updateColor(order, color) }
    }

    fun updateColors(colors: List<Color>) {
        viewModelScope.launch { settingsRepository.updateColors(colors) }
    }
}
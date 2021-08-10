package com.bytepace.dimusco.ui.settings.edit.layers

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytepace.dimusco.data.model.Settings
import com.bytepace.dimusco.data.repository.SettingsRepository
import kotlinx.coroutines.launch

class SettingsEditLayersViewModel : ViewModel() {

    val settings: LiveData<Settings>

    private val settingsRepository = SettingsRepository.get()

    init {
        settings = settingsRepository.settings
    }

    fun onClickBack() {
//        SettingsNavigator.get().back()
    }

    fun onDrawingsChanged(isChecked: Boolean) {
        viewModelScope.launch { settingsRepository.updateIsImageDrawings(isChecked) }
    }

    fun onSymbolsChanged(isChecked: Boolean) {
        viewModelScope.launch { settingsRepository.updateIsImageSymbols(isChecked) }
    }

    fun onTextChanged(isChecked: Boolean) {
        viewModelScope.launch { settingsRepository.updateIsImageText(isChecked) }
    }
}
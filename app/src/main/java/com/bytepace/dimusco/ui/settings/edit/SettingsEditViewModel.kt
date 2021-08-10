package com.bytepace.dimusco.ui.settings.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.model.Settings
import com.bytepace.dimusco.data.repository.SettingsRepository
//import com.bytepace.dimusco.navigation.SettingsNavigator
import kotlinx.coroutines.launch

class SettingsEditViewModel : ViewModel() {

    val settings: LiveData<Settings>

    private val settingsRepository = SettingsRepository.get()

    init {
        settings = settingsRepository.settings
    }

    fun onClickBack() {
//        SettingsNavigator.get().back()
    }

    fun onClickDefaults() {
//        SettingsNavigator.get().navigate(R.id.action_edit_to_defaults)
    }

    fun onClickColors() {
//        SettingsNavigator.get().navigate(R.id.action_edit_to_colors)
    }

    fun onClickSymbols() {
//        SettingsNavigator.get().navigate(R.id.action_edit_to_symbols)
    }

    fun onClickLayers() {
//        SettingsNavigator.get().navigate(R.id.action_edit_to_layers)
    }

    fun setConfirmSavingChanges(confirm: Boolean) {
//        viewModelScope.launch { settingsRepository.updateConfirmSavingChanges(confirm) }
    }
}
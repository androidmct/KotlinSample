package com.bytepace.dimusco.ui.settings.general

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingsGeneralViewModelFactory(private val aid: String) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsGeneralViewModel::class.java)) {
            return SettingsGeneralViewModel(aid) as T
        }
        throw IllegalAccessException()
    }
}
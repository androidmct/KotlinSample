package com.bytepace.dimusco.ui.settings.layers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingsLayersViewModelFactory(private val scoreId: String) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsLayersViewModel::class.java)) {
            return SettingsLayersViewModel(scoreId) as T
        }
        throw IllegalAccessException()
    }
}
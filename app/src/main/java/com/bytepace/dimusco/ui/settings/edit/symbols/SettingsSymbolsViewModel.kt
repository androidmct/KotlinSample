package com.bytepace.dimusco.ui.settings.edit.symbols

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytepace.dimusco.data.model.Settings
import com.bytepace.dimusco.data.model.Symbol
import com.bytepace.dimusco.data.repository.SettingsRepository
import kotlinx.coroutines.launch

class SettingsSymbolsViewModel : ViewModel() {

    val settings: LiveData<Settings>
    val selectedItem = MutableLiveData<Int>(null)

    private val settingsRepository = SettingsRepository.get()

    init {
        settings = settingsRepository.settings
    }

    fun onClickBack() {
//        SettingsNavigator.get().back()
    }

    fun updateSelectedSymbolScale(scale: Float) {
        val index = selectedItem.value ?: return
        val symbol = settings.value?.symbols?.get(index) ?: return
        viewModelScope.launch { settingsRepository.updateSymbol(symbol.copy(scale = scale)) }
    }

    fun updateSymbols(symbols: List<Symbol>) {
        viewModelScope.launch { settingsRepository.updateSymbols(symbols) }
    }
}
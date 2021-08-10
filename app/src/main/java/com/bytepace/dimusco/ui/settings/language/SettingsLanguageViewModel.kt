package com.bytepace.dimusco.ui.settings.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bytepace.dimusco.data.model.Language
//import com.bytepace.dimusco.navigation.SettingsNavigator
import com.bytepace.dimusco.service.LocalizationService
import com.bytepace.dimusco.utils.SingleLiveEvent

class SettingsLanguageViewModel : ViewModel() {

    val language: String?
        get() = LocalizationService.instance.language
    val languages: Map<String, Language>?
        get() = LocalizationService.instance.languages?.toSortedMap()

    val onLanguageChanged: LiveData<Any>
        get() = onLanguageChangedEvent
    private val onLanguageChangedEvent = SingleLiveEvent<Any>()

    fun onClickBack() {
//        SettingsNavigator.get().back()
    }

    fun setLanguage(language: String) {
        LocalizationService.instance.language = language
        onLanguageChangedEvent.call()
    }
}
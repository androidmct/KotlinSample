package com.bytepace.dimusco.data.source.local

import com.bytepace.dimusco.data.source.local.prefs.PrefsHelper

class TranslationLocalDataSource {

    private val prefs: PrefsHelper by lazy {
        PrefsHelper.getInstance()
    }

    fun getLanguage(): String? {
        return prefs.language
    }

    fun setLanguage(key: String) {
        prefs.language = key
    }

    fun getLanguages(): String? {
        return prefs.languages
    }

    fun saveLanguages(languagesJson: String) {
        prefs.languages = languagesJson
    }

    fun getTranslation(language: String): String? {
        return prefs.getTranslation(language)
    }

    fun saveTranslation(language: String, translationJson: String) {
        prefs.saveTranslation(language, translationJson)
    }
}



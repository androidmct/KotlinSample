package com.bytepace.dimusco.data.repository

import com.bytepace.dimusco.data.model.Language
import com.bytepace.dimusco.data.source.local.TranslationLocalDataSource
import com.bytepace.dimusco.data.source.remote.TranslationRemoteDataSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TranslationRepository(private val callback: TranslationsCallback? = null) {

    private val gson by lazy { Gson() }
    private val local by lazy { TranslationLocalDataSource() }
    private val remote by lazy { TranslationRemoteDataSource() }

    fun getLanguage(): String? {
        return local.getLanguage()
    }

    fun setLanguage(key: String) {
        local.setLanguage(key)
    }

    fun getTranslation(key: String): Map<String, String>? {
        return gson.fromJson<Map<String, String>>(
            local.getTranslation(key),
            Map::class.java
        )
    }

    fun getLanguages(): Map<String, Language>? {
        return gson.fromJson<Map<String, Language>>(
            local.getLanguages(),
            object : TypeToken<Map<String, Language>>() {}.type
        )
    }

    fun getTranslations() {
        remote.getTranslations {
            it?.apply {
                for (translation in translations) {
                    saveTranslation(translation.key, translation.value)
                }
                if (languages.isNotEmpty()) {
                    saveLanguages(languages)
                }
                callback?.onGetTranslations(languages)
            }
        }
    }

    private fun saveLanguages(languages: Map<String, Language>) {
        local.saveLanguages(gson.toJson(languages))
    }

    private fun saveTranslation(language: String, dictionary: Map<String, String>) {
        local.saveTranslation(language, gson.toJson(dictionary))
    }

    interface TranslationsCallback {
        fun onGetTranslations(languages: Map<String, Language>)
    }
}
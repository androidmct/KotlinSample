package com.bytepace.dimusco.service

import androidx.lifecycle.LiveData
import com.bytepace.dimusco.data.model.Language
import com.bytepace.dimusco.data.repository.TranslationRepository
import com.bytepace.dimusco.utils.LANG_EN
import com.bytepace.dimusco.utils.SingleLiveEvent

class LocalizationService private constructor() : TranslationRepository.TranslationsCallback {

    companion object {
        val instance = LocalizationService()
    }

    val onLanguageChanged: LiveData<String>
        get() = onLanguageChangedEvent

    var language: String? = null
        set(key) {
            key?.let {
                if (field != key) {
                    repository.setLanguage(key)
                    dictionary = repository.getTranslation(key)
                    field = key
                }
                onLanguageChangedEvent.value = key ?: ""
            }
        }
    var languages: Map<String, Language>? = mapOf()
        private set

    private val repository by lazy { TranslationRepository(this) }
    private var dictionary: Map<String, String>? = mapOf()
    private var onLanguageChangedEvent = SingleLiveEvent<String>()

    init {
        language = repository.getLanguage() ?: LANG_EN
        language?.let {
            dictionary = repository.getTranslation(it)
        }
        languages = repository.getLanguages()
        repository.getTranslations()
    }

    fun getString(key: String): String? {
        return dictionary?.get(key)
    }

    override fun onGetTranslations(languages: Map<String, Language>) {
        this.languages = languages
    }
}

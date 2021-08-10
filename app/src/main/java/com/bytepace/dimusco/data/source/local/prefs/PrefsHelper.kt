package com.bytepace.dimusco.data.source.local.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private const val DIMUSCO_PREFS = "DIMUSCO_PREFS"
private const val KEY_EMAIL = "EMAIL"
private const val KEY_PASSWORD = "PASSWORD"
private const val KEY_USER_ID = "UID"
private const val KEY_DDID = "DDID"
private const val KEY_TOKEN = "TOKEN"
private const val KEY_LANGUAGE = "LANGUAGE"
private const val KEY_LANGUAGES = "LANGUAGES"
private const val KEY_TRANSLATION = "TRANSLATION_"

@SuppressLint("NewApi")
class PrefsHelper private constructor(context: Context) {

    private var prefs: SharedPreferences

    companion object {
        private const val KEY_SIZE = 256
        private lateinit var instance: PrefsHelper

        fun getInstance(): PrefsHelper {
            if (::instance.isInitialized) {
                return instance
            }

            throw UninitializedPropertyAccessException(
                "Call init(Context) before using this method."
            )
        }

        fun init(context: Context) {
            instance = PrefsHelper(context)
        }

    }

    init {

        val builder = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .build()
        val masterKey = MasterKey.Builder(context)
            .setKeyGenParameterSpec(builder)
            .build()
        prefs = EncryptedSharedPreferences.create(
            context, DIMUSCO_PREFS, masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    var email: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) = prefs.edit { putString(KEY_EMAIL, value) }

    var password: String?
        get() = prefs.getString(KEY_PASSWORD, null)
        set(value) = prefs.edit { putString(KEY_PASSWORD, value) }

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit { putString(KEY_USER_ID, value) }

    var ddid: Int
        get() = prefs.getInt(KEY_DDID, -1)
        set(value) = prefs.edit { putInt(KEY_DDID, value) }

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit { putString(KEY_TOKEN, value) }

    var language: String?
        get() = prefs.getString(KEY_LANGUAGE, null)
        set(value) = prefs.edit { putString(KEY_LANGUAGE, value) }

    var languages: String?
        get() = prefs.getString(KEY_LANGUAGES, null)
        set(value) = prefs.edit { putString(KEY_LANGUAGES, value) }

    fun getTranslation(language: String): String? {
        return prefs.getString("$KEY_TRANSLATION$language", null)
    }

    fun saveTranslation(language: String, dictionary: String) {
        prefs.edit { putString("$KEY_TRANSLATION$language", dictionary) }
    }

    fun clearEmail() {
        prefs.edit { remove(KEY_EMAIL) }
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun clearPassword() {
        prefs.edit { remove(KEY_PASSWORD) }
    }

    fun clearSession() {
        prefs.edit {
            remove(KEY_USER_ID)
            remove(KEY_DDID)
            remove(KEY_TOKEN)
        }
    }

    fun clearLanguage() {
        prefs.edit { remove(KEY_LANGUAGE) }
    }
}
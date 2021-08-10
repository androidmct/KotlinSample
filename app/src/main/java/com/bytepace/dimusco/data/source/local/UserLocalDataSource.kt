package com.bytepace.dimusco.data.source.local

import com.bytepace.dimusco.data.model.Session
import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import com.bytepace.dimusco.data.source.local.model.ColorDB
import com.bytepace.dimusco.data.source.local.model.SettingsDB
import com.bytepace.dimusco.data.source.local.model.SymbolDB
import com.bytepace.dimusco.data.source.local.model.UserDB
import com.bytepace.dimusco.data.source.local.prefs.PrefsHelper

class UserLocalDataSource {

    private var userId: String? = null
    private var ddid: Int = -1
    private var token: String? = null

    private val database: DimuscoDatabase by lazy {
        DimuscoDatabase.getInstance()
    }

    private val prefs: PrefsHelper by lazy {
        PrefsHelper.getInstance()
    }

    fun getEmail(): String? {
        return prefs.email
    }

    fun saveEmail(email: String) {
        prefs.email = email
    }

    fun clearEmail() {
        prefs.clearEmail()
    }

    fun getPassword(): String? {
        return prefs.password
    }

    fun savePassword(password: String) {
        prefs.password = password
    }

    fun clearPassword() {
        prefs.clearPassword()
    }

    fun saveUser(
        user: UserDB,
        defaultSettings: SettingsDB,
        defaultColors: List<ColorDB>,
        defaultSymbols: List<SymbolDB>
    ) {
        database.userDao().select(user.email) ?: let { database.userDao().insert(user) }
        database.settingsDao().insert(defaultSettings)
        database.runInTransaction {
            if (database.colorDao().selectFirst(user.uid) == null) {
                database.colorDao().insert(defaultColors)
            }
        }
        database.runInTransaction {
            if (database.symbolDao().selectFirst(user.uid) == null) {
                database.symbolDao().insert(defaultSymbols)
            }
        }
    }

    fun getUser(email: String): UserDB? = database.userDao().select(email)

    fun saveSession(userId: String, ddid: Int, token: String) {
        this.userId = userId
        this.ddid = ddid
        this.token = token
    }

    fun getSession(): Session? {
        val userId = userId
        val ddid = ddid
        val token = token
        return when {
            userId != null && token != null && ddid > 0 -> Session(userId, ddid, token)
            else -> null
        }
    }

    fun clearSession() {
        prefs.clearSession()
    }
}
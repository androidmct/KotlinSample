package com.bytepace.dimusco.data.repository

import com.bytepace.dimusco.data.mapper.ColorMapper
import com.bytepace.dimusco.data.mapper.SettingsMapper
import com.bytepace.dimusco.data.mapper.SymbolMapper
import com.bytepace.dimusco.data.mapper.UserMapper
import com.bytepace.dimusco.data.model.LoginResult
import com.bytepace.dimusco.data.model.Session
import com.bytepace.dimusco.data.model.Settings
import com.bytepace.dimusco.data.model.User
import com.bytepace.dimusco.data.source.local.UserLocalDataSource
import com.bytepace.dimusco.data.source.remote.UserRemoteDataSource
import com.bytepace.dimusco.data.source.remote.model.LoginError
import com.bytepace.dimusco.data.source.remote.model.LoginResponse
import com.bytepace.dimusco.utils.*
import com.google.gson.Gson

class UserRepository private constructor() {
    companion object {
        private val instance by lazy { UserRepository() }

        fun get(): UserRepository {
            return instance
        }
    }

    var currentUser: User? = null

    private val local by lazy { UserLocalDataSource() }
    private val remote by lazy { UserRemoteDataSource() }

    fun getEmail(): String? {
        return local.getEmail()
    }

    fun getPassword(): String? {
        return local.getPassword()
    }

    fun getSession(): Session? {
        return local.getSession()
    }

    fun clearSession() {
        local.clearSession()
    }

    suspend fun login(
        email: String,
        password: String,
        rememberPassword: Boolean,
        callback: LoginCallback
    ) {
        local.clearSession()
        local.clearPassword()
        val user = getLocalUser(email)
        remote.login(email, password, user?.ddid ?: 0,
            { onLoginSuccess(it, password, rememberPassword, callback) },
            { onLoginError(it, callback) }
        )
    }

    fun loginOffline(
        email: String,
        password: String,
        rememberPassword: Boolean,
        callback: LoginCallback
    ) {
        local.clearSession()
        local.clearPassword()
        val user = getLocalUser(email)
        if (user == null || user.password != getSecurePassword(password)) {
            callback.onLogin(LoginResult.Error(STR_ERR_LOGIN_OFFLINE))
        } else {
            if (rememberPassword) {
                local.savePassword(password)
            }
            local.saveEmail(user.email)
            local.saveSession(user.uid, user.ddid, user.token)
            currentUser = UserMapper.fromLocalUser(user)
            callback.onLogin(LoginResult.Success)
        }
    }

    fun logout() {
        currentUser = null
        local.apply {
            clearSession()
            clearEmail()
            clearPassword()
        }
    }

    fun getLocalUser(email: String): com.bytepace.dimusco.data.source.local.model.UserDB? =
        local.getUser(email)

    private fun onLoginSuccess(
        loginResponse: LoginResponse?,
        password: String,
        rememberPassword: Boolean,
        callback: LoginCallback
    ) {
        when (val user = loginResponse?.user) {
            null -> callback.onLogin(LoginResult.Error(null))
            else -> {
                with(loginResponse) {
                    saveUser(ddid, token, user, getSecurePassword(password))
                    if (rememberPassword) {
                        local.savePassword(password)
                    }
                    local.saveEmail(user.email)
                    local.saveSession(user.uid, ddid, token)
                    currentUser = user
                    callback.onLogin(LoginResult.Success)
                }
            }
        }
    }

    private fun saveUser(ddid: Int, token: String, user: User, password: String) {
        local.saveUser(
            UserMapper.toLocalUser(ddid, token, user, password),
            SettingsMapper.toLocalSettings(
                user.uid, Settings(
                    DEFAULT_SCROLLING_HORIZONTAL,
                    DEFAULT_CONFIRM_SAVING_CHANGES,
                    DEFAULT_TRANSPARENCY,
                    DEFAULT_LINE_THICKNESS,
                    DEFAULT_ERASER_THICKNESS,
                    DEFAULT_TEXT_SIZE,
                    DEFAULT_SELECTED_COLOR,
                    DEFAULT_IMAGE_BASED_DRAWINGS,
                    DEFAULT_IMAGE_BASED_SYMBOLS,
                    DEFAULT_IMAGE_BASED_TEXT,
                    DEFAULT_EDIT_TIMEOUT
                )
            ),
            DEFAULT_COLORS.mapIndexed { index, color ->
                ColorMapper.toLocalColor(user.uid, index, color)
            },
            DEFAULT_SYMBOLS.map { SymbolMapper.toLocalSymbol(user.uid, it) }
        )
    }

    private fun onLoginError(message: String?, callback: LoginCallback) {
        val error = Gson().fromJson(message, LoginError::class.java)
        when {
            error == null -> callback.onLogin(LoginResult.Error(null))
            error.detail != null -> callback.onLogin(LoginResult.Error(error.detail))
            error.errors.isNotEmpty() -> callback.onLogin(LoginResult.Error(error.errors[0]))
        }
    }

}
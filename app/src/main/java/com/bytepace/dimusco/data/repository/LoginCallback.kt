package com.bytepace.dimusco.data.repository

import com.bytepace.dimusco.data.model.LoginResult

interface LoginCallback {
    fun onLogin(result: LoginResult)
}
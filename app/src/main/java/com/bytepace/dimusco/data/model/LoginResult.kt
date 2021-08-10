package com.bytepace.dimusco.data.model

sealed class LoginResult {
    object Success : LoginResult()
    data class Error(val error: String?) : LoginResult()
}


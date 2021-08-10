package com.bytepace.dimusco.data.source.remote

import com.bytepace.dimusco.data.source.remote.api.DimuscoApi
import com.bytepace.dimusco.data.source.remote.model.LoginResponse
import retrofit2.awaitResponse
import timber.log.Timber

private const val KEY_EMAIL = "email"
private const val KEY_PASSWORD = "password"
private const val KEY_DDID = "ddid"
private const val KEY_LANGUAGE = "language"

class UserRemoteDataSource {

    private val api: DimuscoApi by lazy {
        DimuscoApi.create()
    }

    suspend fun login(
        email: String,
        password: String,
        ddid: Int,
        success: suspend (LoginResponse?) -> Unit,
        error: (String?) -> Unit
    ) {
        val params = hashMapOf(
            KEY_EMAIL to email,
            KEY_PASSWORD to password,
            KEY_DDID to ddid.toString(),
            KEY_LANGUAGE to "en"
        )
        val result = api.login(params).awaitResponse()
        when (result.isSuccessful) {
            true -> success(result.body())
            else -> {
                val string = result.errorBody()?.string()
                Timber.e(string)
                error(string)
            }
        }
    }
}
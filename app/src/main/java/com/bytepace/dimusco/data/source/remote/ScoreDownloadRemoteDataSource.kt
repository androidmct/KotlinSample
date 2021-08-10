package com.bytepace.dimusco.data.source.remote

import com.bytepace.dimusco.data.source.remote.api.DimuscoApi
import okhttp3.ResponseBody
import retrofit2.awaitResponse
import timber.log.Timber

class ScoreDownloadRemoteDataSource {

    private val api: DimuscoApi by lazy {
        DimuscoApi.create()
    }

    suspend fun requestPagesHttp(
        token: String,
        paid: String,
        success: suspend (ResponseBody?) -> Unit,
        error: suspend (String?) -> Unit
    ) {
        val result = api.requestPages(token, paid).awaitResponse()
        when (result.isSuccessful) {
            true -> success(result.body())
            else -> {
                val message = result.errorBody()?.toString()
                Timber.e(message)
                error(message)
            }
        }
    }
}
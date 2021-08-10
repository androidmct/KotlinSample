package com.bytepace.dimusco.data.source.remote

import com.bytepace.dimusco.data.source.remote.api.DimuscoApi
import com.bytepace.dimusco.data.source.remote.model.TranslationsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TranslationRemoteDataSource {

    private val api: DimuscoApi by lazy {
        DimuscoApi.create()
    }

    fun getTranslations(callback: (TranslationsResponse?) -> Unit) {
        api.getTranslations().enqueue(object : Callback<TranslationsResponse> {
            override fun onFailure(call: Call<TranslationsResponse>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<TranslationsResponse>, response: Response<TranslationsResponse>
            ) {
                if (response.isSuccessful) {
                    callback(response.body())
                }
            }
        })
    }
}
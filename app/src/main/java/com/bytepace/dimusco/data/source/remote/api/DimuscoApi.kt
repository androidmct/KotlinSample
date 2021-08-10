package com.bytepace.dimusco.data.source.remote.api

import com.bytepace.dimusco.data.source.remote.api.http.apiClient
import com.bytepace.dimusco.data.source.remote.model.LoginResponse
import com.bytepace.dimusco.data.source.remote.model.TranslationsResponse
import com.bytepace.dimusco.utils.REST_API_ENDPOINT
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface DimuscoApi {

    companion object {
        fun create(): DimuscoApi {
            return Retrofit.Builder()
                .baseUrl(REST_API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(apiClient)
                .build()
                .create(DimuscoApi::class.java)
        }
    }

    @FormUrlEncoded
    @POST("sync/app-auth/")
    fun login(@FieldMap params: Map<String, String>): Call<LoginResponse>

    @GET("sync/translations/")
    fun getTranslations(): Call<TranslationsResponse>

    @GET("item/download/{token}/{paid}")
    fun requestPages(@Path("token") token: String?, @Path("paid") paid: String?): Call<ResponseBody>

    @GET("item/download/{token}/{paid}")
    suspend fun requestPage(@Path("token") token: String?, @Path("paid") paid: String?): ResponseBody
}
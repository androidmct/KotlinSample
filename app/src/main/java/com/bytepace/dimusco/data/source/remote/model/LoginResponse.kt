package com.bytepace.dimusco.data.source.remote.model

import com.bytepace.dimusco.data.model.User
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("ddid") val ddid: Int,
    @SerializedName("user") val user: User,
    @SerializedName("minimum_allowed_version") val minAllowedVersion: Float
)
package com.bytepace.dimusco.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class LoginError(
    @SerializedName("detail") val detail: String?,
    @SerializedName("non_field_errors") val errors: List<String>
)
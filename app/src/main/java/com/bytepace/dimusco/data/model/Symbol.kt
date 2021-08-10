package com.bytepace.dimusco.data.model

import com.google.gson.annotations.SerializedName

data class Symbol(
    @SerializedName("order") val order: Int,
    @SerializedName("value") val value: Int,
    @SerializedName("scale") val scale: Float
)
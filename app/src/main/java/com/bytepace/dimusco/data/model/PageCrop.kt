package com.bytepace.dimusco.data.model

import com.google.gson.annotations.SerializedName

data class PageCrop(
    @SerializedName("widthCoefficient")
    val widthCoefficient: Float,
    @SerializedName("heightCoefficient")
    val heightCoefficient: Float,
    @SerializedName("xCoefficient")
    val xCoefficient: Float,
    @SerializedName("yCoefficient")
    val yCoefficient: Float,
    @SerializedName("paid")
    val pageId: String
)
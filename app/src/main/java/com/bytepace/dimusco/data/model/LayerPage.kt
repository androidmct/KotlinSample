package com.bytepace.dimusco.data.model

import com.google.gson.annotations.SerializedName

data class LayerPage(
    @SerializedName("layerId") val layerId: String,
    @SerializedName("paid") val paid: String,
    @SerializedName("width") val width: Float,
    @SerializedName("height") val height: Float,
    @SerializedName("pictures") var pictures: List<Picture>
)
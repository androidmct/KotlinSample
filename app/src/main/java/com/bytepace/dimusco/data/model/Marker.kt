package com.bytepace.dimusco.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Marker(
    @SerializedName("scoreId") val scoreId: String,
    @SerializedName("ownerId") val ownerId: String,
    @SerializedName("pageTo") val pageTo: Int,
    @SerializedName("color") val color: Int
) : Serializable
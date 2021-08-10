package com.bytepace.dimusco.data.model

import com.google.gson.annotations.SerializedName

data class Tab(
    @SerializedName("id") val id: String,
    @SerializedName("order") val order: Long,
    @SerializedName("name") val name: String,
    @SerializedName("scoresIds") val scoresIds: MutableList<String> = mutableListOf(),
)
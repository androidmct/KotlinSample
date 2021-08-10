package com.bytepace.dimusco.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Page constructor(
    @SerializedName("id") val id: String,
    @SerializedName("page_number") val pageNumber: Int,
    val isDownloaded: Boolean = false,
    val aid: String
) : Serializable
package com.bytepace.dimusco.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Layer constructor(
    @SerializedName("laid") val laid: String,
    @SerializedName("lid") val lid: String,
    @SerializedName("name") var name: String,
    @SerializedName("type") val type: Int,
    @SerializedName("version") val version: Int,
    @SerializedName("position") var position: Int,
    @SerializedName("sid") val sid: String,
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("visible") val isVisible: Boolean,
    @SerializedName("is_imageBased_drawings") val isImageDrawings: Boolean,
    @SerializedName("is_imageBased_symbols") val isImageSymbols: Boolean,
    @SerializedName("is_imageBased_text") val isImageText: Boolean,
    var isActive: Boolean,
    val token: String?
): Serializable
package com.bytepace.dimusco.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class SocketMessage(
    @SerializedName("data") val data: Any?,
    @SerializedName("type") val type: String?,
    @SerializedName("version") val version: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("md5") val md5: String?,
    @SerializedName("laid") val laid: String?,
    @SerializedName("lid") val lid: String?,
    @SerializedName("token") val token: String?
)
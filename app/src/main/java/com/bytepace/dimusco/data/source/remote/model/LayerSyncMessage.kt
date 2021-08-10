package com.bytepace.dimusco.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class LayerSyncMessage(

    @SerializedName("lid")
    val lid: String,

    @SerializedName("paid")
    val paid: String,

    @SerializedName("data")
    val data: String
)
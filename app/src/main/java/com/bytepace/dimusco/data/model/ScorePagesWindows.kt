package com.bytepace.dimusco.data.model

import com.google.gson.annotations.SerializedName

data class ScorePagesWindows(
    @SerializedName("scoreId")
    val scoreId: String,
    @SerializedName("pageWindows")
    val offsets: List<PageCrop>
)
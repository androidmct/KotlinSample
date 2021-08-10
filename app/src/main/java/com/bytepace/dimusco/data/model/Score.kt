package com.bytepace.dimusco.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Score constructor(
    @SerializedName("sid") val sid: String,
    @SerializedName("aid") val aid: String,
    @SerializedName("name") val name: String,
    @SerializedName("composer") val composer: String,
    @SerializedName("edition") val edition: String,
    @SerializedName("instrument") val instrument: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("nop") val pageCount: Int,
    @SerializedName("is_available") val isAvailable: Boolean,
    @SerializedName("pages") val pages: List<Page>,
    val isDownloadingScoreInProgress: Boolean
) : Serializable {

    var progress: Int = 0

    val isDownloaded: Boolean
        get() = pages.isNotEmpty() && pages.all { page -> page.isDownloaded }

    val downloadedPages: Int
        get() = pages.count { it.isDownloaded }
}
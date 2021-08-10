package com.bytepace.dimusco.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("uid") val uid: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("state") val state: Int,
    @SerializedName("created") val created: String,
    @SerializedName("settings_version") val settingsVersion: Int,
    @SerializedName("library_version") val libraryVersion: Int
) : Serializable
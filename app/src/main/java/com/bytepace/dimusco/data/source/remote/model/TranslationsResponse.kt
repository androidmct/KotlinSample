package com.bytepace.dimusco.data.source.remote.model

import com.bytepace.dimusco.data.model.Language
import com.google.gson.annotations.SerializedName

data class TranslationsResponse(

    @SerializedName("languages")
    val languages: Map<String, Language>,

    @SerializedName("translations")
    val translations: Map<String, Map<String, String>>
)
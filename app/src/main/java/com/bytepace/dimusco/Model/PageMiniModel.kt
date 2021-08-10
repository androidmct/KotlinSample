package com.bytepace.dimusco.Model

import android.os.Parcelable
import com.bytepace.dimusco.data.source.local.model.ComplexPage

data class PageMiniModel(
        val scoreId: String? = "",
        val complexPage: ComplexPage? = null
)
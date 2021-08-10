package com.bytepace.dimusco.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScoreModel(
        var name: String? = "",
        var composer: String? = "",
        var instrument: String? = "",
        var edition: String? = "",
        var downStatus: Boolean? = false
): Parcelable
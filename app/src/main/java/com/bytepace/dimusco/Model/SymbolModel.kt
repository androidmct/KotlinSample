package com.bytepace.dimusco.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SymbolModel(
        var name: String? = "",
        var order: Int? = 0,
        var value: Int? = 0,
        var scale: Float? = 1.0f
): Parcelable
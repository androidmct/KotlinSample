package com.bytepace.dimusco.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LayerModel(
        var name: String? = "",
        var bDrawing: Boolean? = false,
        var bSymbols: Boolean? = false,
        var bText: Boolean? = false,
        var bSelected: Boolean? = false,
        var bShow: Boolean? = false
): Parcelable
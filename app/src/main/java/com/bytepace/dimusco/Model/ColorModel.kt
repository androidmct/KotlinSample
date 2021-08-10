package com.bytepace.dimusco.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ColorModel(
        var value: String? = ""
): Parcelable
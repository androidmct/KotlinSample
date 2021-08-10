package com.bytepace.dimusco.Model

import android.graphics.Bitmap
import android.media.Image
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PageModel(
        var imgOne: Bitmap? = null,
        var imgTwo: Bitmap? = null,
        var imgThree: Bitmap? = null
): Parcelable
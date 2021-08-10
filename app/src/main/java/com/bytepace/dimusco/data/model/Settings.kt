package com.bytepace.dimusco.data.model

import com.google.gson.annotations.SerializedName

// TODO default values
data class Settings(
    @SerializedName("isPageSwitcherHorizontal") val pageScrollingHorizontal: Boolean,
    @SerializedName("isConfirmEdit") val confirmSavingChanges: Boolean = true,
    @SerializedName("defaultAlpha") val transparency: Float,
    @SerializedName("defaultThickness") val lineThickness: Float,
    @SerializedName("defaultEraseThickness") val eraserThickness: Float,
    @SerializedName("defaultFontSize") val textSize: Float,
    @SerializedName("selectedColor") val selectedColor: Int,
    @SerializedName("isImageBasedDrawings") val isImageDrawings: Boolean,
    @SerializedName("isImageBasedSymbols") val isImageSymbols: Boolean,
    @SerializedName("isImageBasedText") val isImageText: Boolean,
    @SerializedName("closeEditorTimeout") val editTimeout: Long,
    @SerializedName("colours") val colors: List<Color> = listOf(),
    @SerializedName("symbols") val symbols: List<Symbol> = listOf(),
    @SerializedName("defaultFontName") val fontName: String? = "Arial",
    @SerializedName("locale") val locale: String? = "i-default",
    @SerializedName("defaultSize") val defaultSize: Float? = 0.5f
)
package com.bytepace.dimusco.data.model

import com.bytepace.dimusco.utils.PICTURE_TYPE_BRUSH
import com.google.gson.annotations.SerializedName

data class Picture constructor(
    @SerializedName("x") var x: Float = 0f,
    @SerializedName("y") var y: Float = 0f,
    @SerializedName("width") var width: Float = 0f,
    @SerializedName("height") var height: Float = 0f,
    @SerializedName("type") val type: String = "",
    @SerializedName("localPath") val filePath: String = "",
    @SerializedName("base64ImageData") val imageData: String = "",
    @SerializedName("color") var color: Int = 0,
    @SerializedName("order") val order: Int = 0,
    @SerializedName("alpha") var transparency: Float = 1f,
    @SerializedName("points") val points: List<PathPoint> = listOf(),
    @SerializedName("text") var text: String = "",
    @SerializedName("symbolType") val symbol: Int = 0,
    @SerializedName("brushSize") var lineThickness: Float = 0f,
    @SerializedName("fontName") val fontName: String = "",
    @SerializedName("fontSize") var fontSize: Float = 0f,
    var updatedAt: Long?
) {
    fun clone(): Picture {
        if (type == PICTURE_TYPE_BRUSH) {
            return this.copy(points = points.map { it.copy() })
        }
        return this.copy()
    }
}


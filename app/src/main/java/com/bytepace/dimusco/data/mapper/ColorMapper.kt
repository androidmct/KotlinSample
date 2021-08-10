package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.source.local.model.ColorDB

object ColorMapper {

    fun toLocalColor(
        userId: String,
        order: Int,
        color: com.bytepace.dimusco.data.model.Color
    ): ColorDB {
        return ColorDB(userId, order, color.value)
    }

    fun fromLocalColor(color: ColorDB): com.bytepace.dimusco.data.model.Color {
        return com.bytepace.dimusco.data.model.Color(color.value)
    }

}
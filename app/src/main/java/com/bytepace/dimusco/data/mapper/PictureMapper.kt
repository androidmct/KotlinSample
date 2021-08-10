package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.model.Picture
import com.bytepace.dimusco.data.source.local.model.PictureWithPoints

object PictureMapper {

    fun toLocalPicture(
        paid: String,
        layerId: String,
        picture: Picture,
        updatedAt: Long
    ): com.bytepace.dimusco.data.source.local.model.PictureDB {
        return com.bytepace.dimusco.data.source.local.model.PictureDB(
            paid,
            layerId,
            picture.x,
            picture.y,
            picture.width,
            picture.height,
            picture.type,
            picture.filePath,
            picture.color,
            picture.order,
            picture.transparency,
            picture.text,
            picture.symbol,
            picture.lineThickness,
            picture.fontName,
            picture.fontSize,
            updatedAt,
            null
        )
    }

    fun toPictureWithPoints(
        paid: String,
        layerId: String,
        picture: Picture,
        updatedAt: Long
    ): PictureWithPoints {
        return PictureWithPoints(
            com.bytepace.dimusco.data.source.local.model.PictureDB(
                paid,
                layerId,
                picture.x,
                picture.y,
                picture.width,
                picture.height,
                picture.type,
                picture.filePath,
                picture.color,
                picture.order,
                picture.transparency,
                picture.text,
                picture.symbol,
                picture.lineThickness,
                picture.fontName,
                picture.fontSize,
                updatedAt,
                null
            ),
            picture.points.mapIndexed { index, point ->
                PathPointMapper.toLocalPoint(point, index)
            }
        )
    }

    fun fromLocalPicture(
        picture: PictureWithPoints
    ): Picture {
        return Picture(
            picture.picture.x,
            picture.picture.y,
            picture.picture.width,
            picture.picture.height,
            picture.picture.type,
            picture.picture.filePath,
            "",
            picture.picture.color,
            picture.picture.order,
            picture.picture.transparency,
            picture.points.map { PathPointMapper.fromLocalPoint(it) },
            picture.picture.text,
            picture.picture.symbol,
            picture.picture.lineThickness,
            picture.picture.fontName,
            picture.picture.fontSize,
            picture.picture.updatedAt
        )
    }

}
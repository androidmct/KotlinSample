package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.model.PathPoint

object PathPointMapper {

    fun toLocalPoint(
        point: PathPoint,
        order: Int
    ): com.bytepace.dimusco.data.source.local.model.PathPointDB {
        return com.bytepace.dimusco.data.source.local.model.PathPointDB(
            0,
            point.x,
            point.y,
            order
        )
    }

    fun fromLocalPoint(point: com.bytepace.dimusco.data.source.local.model.PathPointDB): PathPoint {
        return PathPoint(point.x, point.y)
    }
}
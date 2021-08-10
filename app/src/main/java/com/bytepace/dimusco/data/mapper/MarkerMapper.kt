package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.model.Marker

object MarkerMapper {

    fun toLocalMarker(marker: Marker): com.bytepace.dimusco.data.source.local.model.MarkerDB {
        return com.bytepace.dimusco.data.source.local.model.MarkerDB(
            marker.scoreId,
            marker.ownerId,
            marker.pageTo,
            marker.color
        )
    }

    fun fromLocalMarker(marker: com.bytepace.dimusco.data.source.local.model.MarkerDB): Marker {
        return Marker(
            marker.scoreId,
            marker.ownerId,
            marker.pageTo,
            marker.color
        )
    }

}
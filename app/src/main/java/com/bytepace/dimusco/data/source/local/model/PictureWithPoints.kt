package com.bytepace.dimusco.data.source.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class PictureWithPoints(
    @Embedded
    val picture: PictureDB,
    @Relation(parentColumn = "id", entityColumn = "pictureId")
    val points: List<PathPointDB>
)
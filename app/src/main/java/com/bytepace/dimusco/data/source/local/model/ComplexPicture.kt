package com.bytepace.dimusco.data.source.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class ComplexPicture (
    @Embedded
    val pictureDB: PictureDB,
    @Relation(parentColumn = "layerId", entityColumn = "lid")
    val layerDB: LayerDB,
    @Relation(parentColumn = "id", entityColumn = "pictureId")
    val points: List<PathPointDB>
)
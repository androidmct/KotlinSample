package com.bytepace.dimusco.data.source.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class LayerWithPages(

    @Embedded
    val layer: LayerDB,

    @Relation(parentColumn = "lid", entityColumn = "layerId", entity = LayerPageDB::class)
    val layerPages: List<PageWithPictures>
)
package com.bytepace.dimusco.data.source.local.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation


data class ComplexPage constructor(
    @Embedded
    val page: PageDB,
    @Relation(
        entity = PictureDB::class,
        parentColumn = "id",
        entityColumn = "paid"
    )
    val pictures: List<ComplexPicture>,
    @Relation(parentColumn = "id", entityColumn = "page_id")
    val cropWindows: List<ScorePageWindowDB>
) {
    @Ignore
    val groupedPictures = pictures.groupBy { it.layerDB }
}
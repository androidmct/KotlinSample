package com.bytepace.dimusco.data.source.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class PageWithPictures(

    @Embedded
    val page: LayerPageDB,

    @Relation(parentColumn = "paid", entityColumn = "paid", entity = PictureDB::class)
    val pictures: List<PictureWithPoints>
)
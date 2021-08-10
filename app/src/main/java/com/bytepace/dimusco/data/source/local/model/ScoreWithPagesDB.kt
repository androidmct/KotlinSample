package com.bytepace.dimusco.data.source.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class ScoreWithPagesDB(
    @Embedded
    val score: ScoreDB,
    @Relation(parentColumn = "aid", entityColumn = "aid")
    val pages: List<PageDB>
)
package com.bytepace.dimusco.data.source.local.model

import androidx.room.Entity

@Entity(
    tableName = "marker",
    primaryKeys = ["scoreId", "ownerId", "pageTo"]
)
data class MarkerDB (
    val scoreId: String,

    val ownerId: String,

    val pageTo: Int,

    val color: Int
)
package com.bytepace.dimusco.data.source.local.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "layerpage",
    primaryKeys = ["paid", "layerId"]
)
data class LayerPageDB constructor(

    @ForeignKey(
        entity = PageDB::class,
        parentColumns = ["id"],
        childColumns = ["paid"],
        onDelete = ForeignKey.CASCADE
    ) val paid: String,

    @ForeignKey(
        entity = LayerDB::class,
        parentColumns = ["lid"],
        childColumns = ["layerId"],
        onDelete = ForeignKey.CASCADE
    ) val layerId: String,

    val width: Float,

    val height: Float
)

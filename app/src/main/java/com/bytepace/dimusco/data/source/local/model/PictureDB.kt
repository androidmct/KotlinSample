package com.bytepace.dimusco.data.source.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "picture")
data class PictureDB constructor(

    @ForeignKey(
        entity = LayerPageDB::class,
        parentColumns = ["paid"],
        childColumns = ["paid"],
        onDelete = ForeignKey.CASCADE
    ) val paid: String,

    @ForeignKey(
        entity = LayerPageDB::class,
        parentColumns = ["layerId"],
        childColumns = ["layerId"],
        onDelete = ForeignKey.CASCADE
    ) val layerId: String,

    val x: Float,

    val y: Float,

    val width: Float,

    val height: Float,

    val type: String,

    val filePath: String,

    val color: Int,

    val order: Int,

    val transparency: Float,

    val text: String,

    val symbol: Int,

    val lineThickness: Float,

    val fontName: String,

    val fontSize: Float,

    val updatedAt: Long?,

    @PrimaryKey(autoGenerate = true)
    val id: Int?
)
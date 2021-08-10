package com.bytepace.dimusco.data.source.local.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "pathpoint",
    primaryKeys = ["pictureId", "order"]
)
data class PathPointDB(
    @ForeignKey(
        entity = PictureDB::class,
        parentColumns = ["id"],
        childColumns = ["pictureId"],
        onDelete = ForeignKey.CASCADE
    )
    var pictureId: Long,
    val x: Float,
    val y: Float,
    val order: Int
)
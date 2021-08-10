package com.bytepace.dimusco.data.source.local.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "color",
    primaryKeys = ["userId", "order"]
)
data class ColorDB(

    @ForeignKey(
        entity = UserDB::class,
        parentColumns = ["uid"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    ) val userId: String,

    val order: Int,

    val value: Int
)
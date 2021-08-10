package com.bytepace.dimusco.data.source.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "layer")
data class LayerDB constructor(

    val laid: String,

    @PrimaryKey
    val lid: String,

    val name: String,

    val type: Int,

    val version: Int,

    val position: Int,

    @ForeignKey(
        entity = ScoreDB::class,
        parentColumns = ["sid"],
        childColumns = ["scoreId"],
        onDelete = ForeignKey.CASCADE
    ) val scoreId: String,

    @ForeignKey(
        entity = UserDB::class,
        parentColumns = ["uid"],
        childColumns = ["userId"]
    ) val userId: String,

    val createdBy: String,

    val isVisible: Boolean,

    val isImageDrawings: Boolean,

    val isImageSymbols: Boolean,

    val isImageText: Boolean,

    var isActive: Boolean,

    val token: String?
)
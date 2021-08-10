package com.bytepace.dimusco.data.source.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "page")
data class PageDB constructor(
    @PrimaryKey
    val id: String,
    @ForeignKey(
        entity = ScoreDB::class,
        parentColumns = ["aid"],
        childColumns = ["aid"]
    )
    val aid: String,
    val pageNumber: Int,
    val isDownloaded: Boolean,
    val filename: String
)
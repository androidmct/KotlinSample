package com.bytepace.dimusco.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "scoredto",
    foreignKeys = [
        ForeignKey(
            entity = UserDB::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class ScoreDB constructor(
    val sid: String,
    val userId: String,
    @PrimaryKey
    val aid: String,
    val name: String,
    val composer: String,
    val edition: String,
    val instrument: String,
    val icon: String,
    val pageCount: Int,
    val isAvailable: Boolean,
    val isDownloadingScoreInProgress: Boolean,
    @ColumnInfo(name = "tabId", defaultValue = TabDB.DEFAULT_ID)
    val tabId: String? = null
)
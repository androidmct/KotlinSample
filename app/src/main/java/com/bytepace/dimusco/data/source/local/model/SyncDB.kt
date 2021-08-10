package com.bytepace.dimusco.data.source.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync")
data class SyncDB(
    val userId: String,
    val type: String,
    val refId: String,
    val message: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)
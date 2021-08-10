package com.bytepace.dimusco.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bytepace.dimusco.data.repository.convertors.ListStringConverter

@Entity(tableName = "Tabs")
data class TabDB constructor(
    @PrimaryKey
    @ColumnInfo(name = "id") val tid: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "userId") val userId: String,
    @ColumnInfo(name = "ordering") val order: Long,
    @TypeConverters(ListStringConverter::class)
    @ColumnInfo(name = "scoresIds") val scoreIds: MutableList<String>?,
    @ColumnInfo(name = "isOffline") val isOffline: Boolean = false
) {

    companion object {
        const val DEFAULT_ID = "entity.default"
    }
}
package com.bytepace.dimusco.data.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "score_page_windows",
    primaryKeys = ["page_id", "score_id"],
    foreignKeys = [
        ForeignKey(
            entity = PageDB::class,
            childColumns = ["page_id"],
            parentColumns = ["id"]
        )
    ]
)
data class ScorePageWindowDB constructor(
    @ColumnInfo(name = "width_coefficient")
    val widthCoefficient: Float,
    @ColumnInfo(name = "height_coefficient")
    val heightCoefficient: Float,
    @ColumnInfo(name = "x_coefficient")
    val xCoefficient: Float,
    @ColumnInfo(name = "y_coefficient")
    val yCoefficient: Float,
    @ColumnInfo(name = "page_id")
    val pageId: String,
    @ColumnInfo(name = "score_id")
    val scoreId: String
) {

    companion object {
        const val NO_ID = 0
    }
}
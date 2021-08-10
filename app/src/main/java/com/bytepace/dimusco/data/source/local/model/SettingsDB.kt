package com.bytepace.dimusco.data.source.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsDB(

    @PrimaryKey
    val userId: String,

    val pageScrollingHorizontal: Boolean,

    val confirmSavingChanges: Boolean,

    val transparency: Float,

    val lineThickness: Float,

    val eraserThickness: Float,

    val textSize: Float,

    val selectedColor: Int,

    val isImageDrawings: Boolean,

    val isImageSymbols: Boolean,

    val isImageText: Boolean,

    val editTimeout: Long
)
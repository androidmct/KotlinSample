package com.bytepace.dimusco.data.source.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserDB(

    @ForeignKey(
        entity = UserDB::class,
        parentColumns = ["userId"],
        childColumns = ["uid"],
        onDelete = ForeignKey.CASCADE
    )
    @PrimaryKey
    val uid: String,
    val ddid: Int,
    val token: String,
    val email: String,
    val name: String,
    val password: String,
    val state: Int,
    val created: String,
    val settingsVersion: Int,
    val libraryVersion: Int
)
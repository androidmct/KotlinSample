package com.bytepace.dimusco.data.source.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bytepace.dimusco.data.source.local.model.PictureDB

@Dao
interface PictureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pictures: List<PictureDB>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(picture: PictureDB): Long

    @Query("DELETE FROM picture WHERE paid = :paid AND layerId = :layerId")
    fun delete(paid: String, layerId: String)

    @Query("UPDATE picture SET layerId = :newId WHERE layerId = :oldId")
    fun updateLayerId(oldId: String, newId: String)

    @Query("DELETE FROM picture")
    suspend fun clearTable()
}
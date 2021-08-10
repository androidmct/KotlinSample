package com.bytepace.dimusco.data.source.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bytepace.dimusco.data.source.local.model.MarkerDB

@Dao
interface MarkerDao {
    @Query("SELECT * FROM marker WHERE ownerId = :ownerId AND scoreId = :scoreId AND pageTo = :pageFrom")
    fun select(ownerId: String, scoreId: String, pageFrom: Int): LiveData<MarkerDB>

    @Query("SELECT * FROM marker WHERE ownerId = :ownerId AND scoreId = :scoreId")
    fun selectByScoreId(ownerId: String, scoreId: String): LiveData<List<MarkerDB>>

    @Query("SELECT * FROM marker WHERE ownerId = :ownerId")
    fun selectByOwnerId(ownerId: String): LiveData<List<MarkerDB>>

    @Query("SELECT * FROM marker WHERE ownerId = :ownerId AND scoreId = :scoreId")
    fun selectByOwnerIdSync(ownerId: String, scoreId: String): List<MarkerDB>

    @Query("SELECT * FROM marker")
    fun selectAllSync(): List<MarkerDB>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(marker: MarkerDB)

    @Update
    fun update(marker: MarkerDB)

    @Delete
    fun delete(marker: MarkerDB)

    @Query("DELETE FROM marker")
    suspend fun clearTable()
}
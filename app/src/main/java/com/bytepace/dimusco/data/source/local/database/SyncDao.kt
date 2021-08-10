package com.bytepace.dimusco.data.source.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bytepace.dimusco.data.source.local.model.SyncDB

@Dao
interface SyncDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sync: SyncDB): Long

    @Query("DELETE FROM sync WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE FROM sync WHERE userId = :userId AND refId = :refId")
    fun delete(userId: String, refId: String): Int

    @Query("DELETE FROM sync WHERE userId = :userId AND type = :type AND refId = :refId")
    fun delete(userId: String, type: String, refId: String): Int

    @Query("SELECT * FROM sync WHERE userId = :userId")
    fun select(userId: String): List<SyncDB>

    @Query("SELECT * FROM sync WHERE userId = :userId AND type IN(:types)")
    fun select(userId: String, vararg types: String): SyncDB

    @Query("DELETE FROM sync")
    suspend fun clearTable()
}

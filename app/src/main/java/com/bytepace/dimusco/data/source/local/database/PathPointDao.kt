package com.bytepace.dimusco.data.source.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bytepace.dimusco.data.source.local.model.PathPointDB

@Dao
interface PathPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(points: List<PathPointDB>)

    @Query("DELETE FROM pathpoint")
    suspend fun clearTable()
}
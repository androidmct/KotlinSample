package com.bytepace.dimusco.data.source.local.database

import androidx.room.*
import com.bytepace.dimusco.data.source.local.model.ColorDB

@Dao
interface ColorDao {

    @Query("SELECT * FROM color WHERE userId = :userId AND `order` = 0")
    fun selectFirst(userId: String): ColorDB?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(colors: List<ColorDB>)

    @Update
    fun update(color: ColorDB)

    @Query("DELETE FROM color WHERE userId = :userId AND `order` NOT IN (:orders)")
    fun deleteRedundant(userId: String, orders: List<Int>)

    @Query("DELETE FROM color")
    suspend fun clearTable()
}

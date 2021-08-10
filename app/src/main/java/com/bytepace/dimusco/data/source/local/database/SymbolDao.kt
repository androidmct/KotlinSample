package com.bytepace.dimusco.data.source.local.database

import androidx.room.*
import com.bytepace.dimusco.data.source.local.model.SymbolDB

@Dao
interface SymbolDao {

    @Query("SELECT * FROM symbol WHERE userId = :userId AND `order` = 0")
    fun selectFirst(userId: String): SymbolDB?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(symbols: List<SymbolDB>)

    @Update
    fun update(symbol: SymbolDB)

    @Query("DELETE FROM symbol WHERE userId = :userId AND `order` NOT IN (:orders)")
    fun deleteRedundant(userId: String, orders: List<Int>)

    @Query("DELETE FROM symbol")
    suspend fun clearTable()
}

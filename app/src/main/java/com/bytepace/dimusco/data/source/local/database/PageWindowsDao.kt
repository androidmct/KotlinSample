package com.bytepace.dimusco.data.source.local.database

import androidx.room.*
import com.bytepace.dimusco.data.source.local.model.ScorePageWindowDB

@Dao
interface PageWindowsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg data: ScorePageWindowDB)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg data: ScorePageWindowDB)

    @Query("DELETE FROM score_page_windows")
    suspend fun deleteAll()

    @Query("DELETE FROM score_page_windows WHERE page_id LIKE :paid AND score_id LIKE :sid")
    suspend fun deleteByPageScoreId(paid: String, sid: String)

    @Query("SELECT * FROM score_page_windows WHERE page_id LIKE :paid AND score_id LIKE :sid")
    suspend fun selectByPageAndScoreId(paid: String, sid: String): List<ScorePageWindowDB>

    @Query("SELECT * FROM score_page_windows")
    suspend fun selectAll(): List<ScorePageWindowDB>
}
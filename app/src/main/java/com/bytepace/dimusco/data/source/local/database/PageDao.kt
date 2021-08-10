package com.bytepace.dimusco.data.source.local.database

import androidx.room.*
import com.bytepace.dimusco.data.source.local.model.ComplexPage
import com.bytepace.dimusco.data.source.local.model.PageDB
import kotlinx.coroutines.flow.Flow

@Dao
interface PageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(scores: List<PageDB>)

    @Query("UPDATE page SET isDownloaded = 0 WHERE aid LIKE :aid")   //SQLite does not have a boolean data type. Room maps it to an INTEGER column, mapping true to 1 and false to 0.
    suspend fun setNotDownloaded(aid: String)

    @Query("UPDATE page SET isDownloaded = 1 WHERE id LIKE :id")
    suspend fun setDownloaded(id: String)

    @Query("DELETE FROM page")
    suspend fun clearTable()

    @Transaction
    @Query("SELECT * FROM page WHERE aid LIKE :aid")
    fun retrieveScorePages(aid: String): Flow<List<ComplexPage>>

    @Transaction
    @Query("SELECT * FROM page WHERE id LIKE :paid LIMIT 1")
    fun subscribeForPage(paid: String): Flow<ComplexPage?>
}
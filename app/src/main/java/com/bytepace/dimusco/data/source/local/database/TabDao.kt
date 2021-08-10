package com.bytepace.dimusco.data.source.local.database

import androidx.room.*
import com.bytepace.dimusco.data.source.local.model.TabDB
import kotlinx.coroutines.flow.Flow


@Dao
interface TabDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg tabs: TabDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg tabs: TabDB)

    @Query("UPDATE tabs SET name = :name WHERE id LIKE :id")
    suspend fun updateTabName(id: String, name: String)

    @Query("SELECT MAX(ordering) FROM tabs")
    suspend fun count(): Int?

    @Query("SELECT * FROM tabs WHERE id NOT LIKE '${TabDB.DEFAULT_ID}' ORDER BY ordering ASC")
    fun selectAll(): Flow<List<TabDB>>

    @Query("SELECT * FROM tabs WHERE scoresIds LIKE '%' || :scoreId || '%'  ORDER BY ordering ASC")
    fun selectAllWithScores(scoreId: String): Flow<List<TabDB>>

    @Query("SELECT * FROM tabs WHERE id == :tabId")
    fun selectTabById(tabId: String): Flow<TabDB?>

    @Query("SELECT * FROM tabs WHERE id == :tabId")
    fun selectById(tabId: String): TabDB?

    @Delete
    suspend fun delete(vararg tab: TabDB)

    @Query("DELETE FROM tabs WHERE id LIKE :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM tabs WHERE userId LIKE :userId AND id NOT IN (:tabs) AND isOffline = 0")
    suspend fun deleteAllByUserId(userId: String, tabs: List<String>)

    @Query("DELETE FROM tabs")
    suspend fun deleteAll()

    @Transaction
    suspend fun upsert(vararg tabs: TabDB) {
        insert(*tabs)
        update(*tabs)
    }

    @Query("SELECT * FROM tabs WHERE tabs.id NOT LIKE '${TabDB.DEFAULT_ID}' AND tabs.scoresIds IS NOT NULL")
    fun selectDefaultIds(): Flow<List<TabDB>>

    @Query("DELETE FROM tabs")
    suspend fun clearTable()
}
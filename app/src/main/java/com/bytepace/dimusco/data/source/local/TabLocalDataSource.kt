package com.bytepace.dimusco.data.source.local

import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import com.bytepace.dimusco.data.source.local.database.TabDao
import com.bytepace.dimusco.data.source.local.model.TabDB
import kotlinx.coroutines.flow.Flow

object TabLocalDataSource : TabDao {

    private val database: DimuscoDatabase by lazy {
        DimuscoDatabase.getInstance()
    }

    override suspend fun insert(vararg tabs: TabDB) {
        database.tabDao().insert(*tabs)
    }

    override suspend fun update(vararg tabs: TabDB) {
        database.tabDao().update(*tabs)
    }

    override suspend fun updateTabName(id: String, name: String) {
        database.tabDao().updateTabName(id, name)
    }

    override suspend fun count(): Int {
        return database.tabDao().count() ?: 0
    }

    override fun selectAll(): Flow<List<TabDB>> {
        return database.tabDao().selectAll()
    }

    override fun selectAllWithScores(scoreId: String): Flow<List<TabDB>> {
        return database.tabDao().selectAllWithScores(scoreId)
    }

    override fun selectTabById(tabId: String): Flow<TabDB?> =
        database.tabDao().selectTabById(tabId)

    override fun selectById(tabId: String): TabDB? = database.tabDao().selectById(tabId)

    override suspend fun delete(vararg tab: TabDB) {
        return database.tabDao().delete(*tab)
    }

    override suspend fun deleteById(id: String) {
        database.tabDao().deleteById(id)
    }

    override suspend fun deleteAllByUserId(userId: String, tabs: List<String>) {
        database.tabDao().deleteAllByUserId(userId, tabs)
    }

    override suspend fun deleteAll() {
        database.tabDao().deleteAll()
    }

    override suspend fun upsert(vararg tabs: TabDB) {
        database.tabDao().upsert(*tabs)
    }

    override fun selectDefaultIds(): Flow<List<TabDB>> = database.tabDao().selectDefaultIds()

    override suspend fun clearTable() {
        database.tabDao().clearTable()
    }
}
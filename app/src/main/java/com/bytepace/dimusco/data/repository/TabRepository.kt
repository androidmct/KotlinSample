package com.bytepace.dimusco.data.repository

import com.bytepace.dimusco.data.mapper.TabMapper
import com.bytepace.dimusco.data.model.Tab
import com.bytepace.dimusco.data.source.local.TabLocalDataSource
import com.bytepace.dimusco.data.source.local.UserLocalDataSource
import java.util.*

object TabRepository {

    private val dataSource = TabLocalDataSource
    private val userLocalDataSource = UserLocalDataSource()

    suspend fun createNew() {
        val count = dataSource.count()
        dataSource.insert(
            com.bytepace.dimusco.data.source.local.model.TabDB(
                UUID.randomUUID().toString(),
                "New Tab",
                userLocalDataSource.getUser(userLocalDataSource.getEmail()!!)?.uid!!,
                count + 1L,
                null,
                true
            )
        )
    }

    fun selectAll() = dataSource.selectAll()

    suspend fun update(id: String, name: String) = dataSource.updateTabName(id, name)

    suspend fun delete(id: String) = dataSource.deleteById(id)

    suspend fun deleteAll() = dataSource.deleteAll()

    suspend fun upsertTabs(tabs: List<Tab>) {
        val userId = userLocalDataSource.getUser(userLocalDataSource.getEmail()!!)?.uid!!
        val tabsDTO = tabs.map { TabMapper.toLocalTab(it, userId) }
        dataSource.deleteAllByUserId(userId, tabsDTO.map { it.tid })
        dataSource.upsert(*tabsDTO.toTypedArray())
    }
}
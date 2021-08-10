package com.bytepace.dimusco.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.withTransaction
import com.bytepace.dimusco.data.mapper.PageMapper
import com.bytepace.dimusco.data.mapper.ScoreMapper
import com.bytepace.dimusco.data.model.Score
import com.bytepace.dimusco.data.repository.score.SORT_BY_NAME_ASC
import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import com.bytepace.dimusco.data.source.local.model.PageDB
import com.bytepace.dimusco.data.source.local.model.ScoreDB
import com.bytepace.dimusco.data.source.local.model.ScoreWithPagesDB
import com.bytepace.dimusco.data.source.local.model.TabDB
import com.bytepace.dimusco.utils.deleteFile
import com.bytepace.dimusco.utils.readFile
import kotlinx.coroutines.flow.*

class ScoreLocalDataSource {

    private val database: DimuscoDatabase by lazy {
        DimuscoDatabase.getInstance()
    }
    private val scoresDao = database.scoreDao()
    private val tabsDao = database.tabDao()

    fun selectAllScores(
        userId: String,
        sortType: Int = SORT_BY_NAME_ASC
    ): Flow<List<ScoreWithPagesDB>> {
        return scoresDao.selectAllScores(userId, sortType)
    }

    fun getScoresByUserAndTab(
        userId: String,
        tabId: String,
        sortType: Int
    ): Flow<List<ScoreWithPagesDB>> =
        when (tabId) {
            TabDB.DEFAULT_ID -> tabsDao.selectDefaultIds()
                .map { items ->
                    items.mapNotNull { it.scoreIds }.flatten().toSet()
                }
                .flatMapLatest {
                    when (it.isEmpty()) {
                        false -> scoresDao.selectDefaultUsersScores(userId, it.toList(), sortType)
                        else -> scoresDao.selectAllScores(userId, sortType)
                    }
                }
            else -> tabsDao.selectTabById(tabId)
                .flatMapLatest { tab ->
                    val scores = tab?.scoreIds?.let {
                        scoresDao.selectUserTabScore(userId, it, sortType)
                    }
                    scores ?: emptyFlow()
                }
        }

    fun getScore(aid: String): LiveData<Score> {
        return scoresDao.selectScore(aid).map { ScoreMapper.fromLocalScore(it) }
    }

    fun getPageFile(filename: String): ByteArray? = readFile(filename)

    suspend fun update(list: List<ScoreDB>) {
        scoresDao.insert(list)
    }

    suspend fun saveScores(userId: String, scores: List<Score>) {
        val listPages = arrayListOf<PageDB>()
        val tabs = tabsDao.selectDefaultIds().first()
        val listScores = scores.map { score ->
            val tabId = tabs.firstOrNull { it.scoreIds?.contains(score.sid) ?: false }
                ?.tid
            val scoreDTO = ScoreMapper.toLocalScore(score, userId)
                .copy(tabId = tabId)
            listPages.addAll(score.pages.map { PageMapper.toLocalPage(it, score.aid) })
            scoreDTO
        }
        database.withTransaction {
            scoresDao.deleteRedundant(userId, scores.map { it.aid })
            scoresDao.insert(listScores)
            database.pageDao().insert(listPages)
        }
    }

    suspend fun deletePages(aid: String, pageIds: List<String>) {
        for (id in pageIds) {
            deleteFile(id)
        }
        database.pageDao().setNotDownloaded(aid)
    }

    suspend fun removeScoreFromTab(scoreId: String) {
        val tabs = tabsDao.selectAllWithScores(scoreId)
            .onEach { tabs -> tabs.map { it.copy(scoreIds = it.scoreIds?.also { it.remove(scoreId) }) } }
            .first()
        database.withTransaction {
            tabsDao.upsert(*tabs.toTypedArray())
            scoresDao.updateScoreTab(scoreId, null)
        }
    }

    suspend fun addScoreToTab(scoreId: String, tabId: String?) {
        if (tabId == null) {
            scoresDao.updateScoreTab(scoreId, tabId)
            return
        }
        val tab = tabsDao.selectById(tabId) ?: return
        val scores = (tab.scoreIds?.toMutableSet() ?: mutableSetOf())
            .also { it.add(scoreId) }
            .toMutableList()
        database.withTransaction {
            tabsDao.upsert(tab.copy(scoreIds = scores))
            scoresDao.updateScoreTab(scoreId, tabId)
        }
    }

    suspend fun setPageIsDownloaded(pageId: String) {
        database.pageDao().setDownloaded(pageId)
    }

    suspend fun setScoreIsDownloaded(aid: String, isDownloaded: Boolean) {
        scoresDao.setIsScoreInProgress(aid, isDownloaded)
    }
}
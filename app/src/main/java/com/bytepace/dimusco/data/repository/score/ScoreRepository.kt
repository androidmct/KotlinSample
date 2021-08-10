package com.bytepace.dimusco.data.repository.score

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.bytepace.dimusco.data.mapper.ScoreMapper
import com.bytepace.dimusco.data.model.Score
import com.bytepace.dimusco.data.source.local.ScoreLocalDataSource
import com.bytepace.dimusco.utils.THUMBNAILS_DIR
import com.bytepace.dimusco.utils.getOptimizedImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class ScoreRepository private constructor(private val userId: String) {

    companion object {
        private lateinit var instance: ScoreRepository

        fun init(userId: String) {
            instance =
                ScoreRepository(userId)
        }

        fun get(): ScoreRepository {
            if (Companion::instance.isInitialized) {
                return instance
            }
            throw UninitializedPropertyAccessException(
                "Call init(String) before using this method."
            )
        }
    }

    var currentScorePageSequence: List<Int>? = null
    var currentScoreRawPageSequence: String? = null

    private val local = ScoreLocalDataSource()

    fun selectAllScores() = local.selectAllScores(userId)

    fun getScore(aid: String): LiveData<Score> = local.getScore(aid)

    fun subscribeForLocalScores(
        userId: String,
        tabId: String,
        sorting: Sorting
    ): Flow<List<Score>> =
        local.getScoresByUserAndTab(userId, tabId, sorting.key)
            .flowOn(Dispatchers.IO)
            .map { scores -> scores.map {
                ScoreMapper.fromLocalScore(it)
            } }

    fun getPageImage(filename: String): Bitmap? {   //
        return local.getPageFile(filename)?.let { getOptimizedImage(it) }
    }

    fun getPageThumbnail(filename: String): Bitmap? {
        return getPageImage("$THUMBNAILS_DIR$filename")
    }

    suspend fun deletePages(aid: String, pagesId: List<String>) {
        local.deletePages(aid, pagesId)
    }

    suspend fun savePages(scores: List<Score>) {
        local.saveScores(userId, scores)
    }

    suspend fun removeScoreFromTab(scoreId: String) {
        local.removeScoreFromTab(scoreId)
    }

    suspend fun addScoreToTab(scoreId: String, tabId: String?) {
        local.addScoreToTab(scoreId, tabId)
    }

    suspend fun setPageIsDownloaded(pageId: String) {
        local.setPageIsDownloaded(pageId)
    }

    suspend fun setScoreIsDownloadInProgress(aid: String, isInProgress: Boolean) {
        local.setScoreIsDownloaded(aid, isInProgress)
    }
}





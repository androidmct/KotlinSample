package com.bytepace.dimusco.data.source.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bytepace.dimusco.data.repository.score.SORT_BY_COMPOSER_ASC
import com.bytepace.dimusco.data.repository.score.SORT_BY_COMPOSER_DESC
import com.bytepace.dimusco.data.repository.score.SORT_BY_NAME_ASC
import com.bytepace.dimusco.data.repository.score.SORT_BY_NAME_DESC
import com.bytepace.dimusco.data.source.local.model.ScoreDB
import com.bytepace.dimusco.data.source.local.model.ScoreWithPagesDB
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {

    @Transaction
    @Query(
        "SELECT * FROM scoredto WHERE userId LIKE :userId AND sid NOT IN (:scoreIds) ORDER BY"
                + " CASE WHEN :sortType = $SORT_BY_COMPOSER_ASC THEN composer END ASC,"
                + " CASE WHEN :sortType = $SORT_BY_COMPOSER_DESC THEN composer END DESC,"
                + " CASE WHEN :sortType = $SORT_BY_NAME_ASC THEN name END ASC,"
                + " CASE WHEN :sortType = $SORT_BY_NAME_DESC THEN name END DESC"
    )
    fun selectDefaultUsersScores(
        userId: String,
        scoreIds: List<String>,
        sortType: Int
    ): Flow<List<ScoreWithPagesDB>>

    @Transaction
    @Query(
        "SELECT * FROM scoredto WHERE userId LIKE :userId ORDER BY"
                + " CASE WHEN :sortType = $SORT_BY_COMPOSER_ASC THEN composer END ASC,"
                + " CASE WHEN :sortType = $SORT_BY_COMPOSER_DESC THEN composer END DESC,"
                + " CASE WHEN :sortType = $SORT_BY_NAME_ASC THEN name END ASC,"
                + " CASE WHEN :sortType = $SORT_BY_NAME_DESC THEN name END DESC"
    )
    fun selectAllScores(userId: String, sortType: Int): Flow<List<ScoreWithPagesDB>>

    @Transaction
    @Query("SELECT * FROM scoredto WHERE aid LIKE :aid")
    fun selectScore(aid: String): LiveData<ScoreWithPagesDB>

    @Query(
        "SELECT * FROM scoredto WHERE userId LIKE :userId AND sid IN (:scoreIds) ORDER BY"
                + " CASE WHEN :sortType = $SORT_BY_COMPOSER_ASC THEN composer END ASC,"
                + " CASE WHEN :sortType = $SORT_BY_COMPOSER_DESC THEN composer END DESC,"
                + " CASE WHEN :sortType = $SORT_BY_NAME_ASC THEN name END ASC,"
                + " CASE WHEN :sortType = $SORT_BY_NAME_DESC THEN name END DESC"
    )
    fun selectUserTabScore(userId: String, scoreIds: List<String>, sortType: Int): Flow<List<ScoreWithPagesDB>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scores: List<ScoreDB>)

    @Query("SELECT * FROM scoredto WHERE userId LIKE :scoreId")
    suspend fun getScoreById(scoreId: String): ScoreDB?

    @Query("DELETE FROM scoredto WHERE userId LIKE :userId AND aid NOT IN (:aids)")
    suspend fun deleteRedundant(userId: String, aids: List<String>)

    @Query("UPDATE scoredto SET tabId = :tabId WHERE aid LIKE :aid")
    suspend fun updateScoreTab(aid: String, tabId: String?)

    @Query("UPDATE scoredto SET isDownloadingScoreInProgress = 0 WHERE aid LIKE :aid")
    suspend fun setNotScoreDownloadedProgress(aid: String)

    @Query("UPDATE scoredto SET isDownloadingScoreInProgress = 1 WHERE aid LIKE :aid")
    suspend fun setScoreDownloadedProgress(aid: String)

    @Query("UPDATE scoredto SET isDownloadingScoreInProgress = :isInProgress WHERE aid LIKE :aid")
    suspend fun setIsScoreInProgress(aid: String, isInProgress: Boolean)

    @Query("DELETE FROM scoredto")
    suspend fun clearTable()
}
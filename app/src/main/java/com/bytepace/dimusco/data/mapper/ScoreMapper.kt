package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.model.Score
import com.bytepace.dimusco.data.source.local.model.ScoreDB
import com.bytepace.dimusco.data.source.local.model.ScoreWithPagesDB

object ScoreMapper {

    fun toLocalScore(score: Score, userId: String): ScoreDB = ScoreDB(
        score.sid,
        userId,
        score.aid,
        score.name,
        score.composer,
        score.edition,
        score.instrument,
        score.icon,
        score.pages.size,
        score.isAvailable,
        score.isDownloadingScoreInProgress
    )

    fun fromLocalScore(score: ScoreWithPagesDB): Score = Score(
        score.score.sid,
        score.score.aid,
        score.score.name,
        score.score.composer,
        score.score.edition,
        score.score.instrument,
        score.score.icon,
        score.score.pageCount,
        score.score.isAvailable,
        score.pages.map { page -> PageMapper.fromLocalPage(page) },
        score.score.isDownloadingScoreInProgress
    )

}
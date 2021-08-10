package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.model.PageCrop
import com.bytepace.dimusco.data.model.ScorePagesWindows
import com.bytepace.dimusco.data.source.local.model.ScorePageWindowDB

object PageWindowMapper {

    fun map(list: List<ScorePagesWindows>) = list.map { score ->
        score.offsets.map {
            ScorePageWindowDB(
                it.widthCoefficient,
                it.heightCoefficient,
                it.xCoefficient,
                it.yCoefficient,
                it.pageId,
                score.scoreId
            )
        }
    }.flatten()

    fun mapLocal(list: List<ScorePageWindowDB>) = list.groupBy {
        it.scoreId
    }.entries.mapNotNull { (score, list) ->
        val offsets = list.map {
            PageCrop(
                it.widthCoefficient,
                it.heightCoefficient,
                it.xCoefficient,
                it.yCoefficient,
                it.pageId
            )
        }
        score?.let { ScorePagesWindows(it, offsets) }
    }
}
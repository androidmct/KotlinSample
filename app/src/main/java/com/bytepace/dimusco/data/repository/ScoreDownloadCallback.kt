package com.bytepace.dimusco.data.repository

import com.bytepace.dimusco.data.model.Score

interface ScoreDownloadCallback {
    fun onScoreDownload(score: Score)
}
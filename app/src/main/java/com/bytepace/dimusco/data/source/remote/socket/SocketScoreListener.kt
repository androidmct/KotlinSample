package com.bytepace.dimusco.data.source.remote.socket

import com.bytepace.dimusco.data.model.Score

interface SocketScoreListener {
    fun onGetScores(scores: List<Score>)
}
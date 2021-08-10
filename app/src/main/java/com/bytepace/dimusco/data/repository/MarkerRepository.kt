package com.bytepace.dimusco.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.bytepace.dimusco.data.mapper.MarkerMapper
import com.bytepace.dimusco.data.model.Marker
import com.bytepace.dimusco.data.source.local.MarkerLocalDataSource
import com.bytepace.dimusco.data.source.remote.MarkerRemoteDataSource
import com.bytepace.dimusco.data.source.remote.socket.SocketMarkersListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class MarkerRepository private constructor(
    private val userId: String
) : SocketMarkersListener {

    companion object {
        private lateinit var instance: MarkerRepository

        fun init(userId: String) {
            instance = MarkerRepository(userId)
        }

        fun get(): MarkerRepository {
            if (::instance.isInitialized) {
                return instance
            }
            throw UninitializedPropertyAccessException(
                "Call init(String) before using this method."
            )
        }
    }

    val markers: LiveData<List<Marker>>
        get() = getTransformedMarkers()

    private val local = MarkerLocalDataSource
    private val remote = MarkerRemoteDataSource(this)

    private fun getTransformedMarkers(): LiveData<List<Marker>> {
        return Transformations.map(local.getMarkers(userId)) { dbScores ->
            dbScores.map { MarkerMapper.fromLocalMarker(it) }
        }
    }

    private suspend fun sendMarkers(markers: List<Marker>, aid: String) {
        withContext(coroutineContext + Dispatchers.IO) {
            val allMarkers =
                if (markers.isNotEmpty()) getMarkersSync(markers.first().scoreId) else emptyList()
            remote.createMarker(allMarkers, aid)
        }
    }

    suspend fun saveMarkers(markers: List<Marker>, aid: String) {
        local.saveMarkers(markers.map { MarkerMapper.toLocalMarker(it) })
        sendMarkers(markers, aid)
    }

    suspend fun deleteMarkers(markers: List<Marker>, aid: String) {
        local.deleteMarkers(markers.map { MarkerMapper.toLocalMarker(it) })
        val scoreIds = markers.map { it.scoreId }.distinct()
        val consistentlyMarkers = scoreIds.map { getMarkersSync(it) }.first()
        sendMarkers(consistentlyMarkers, aid)
    }

    suspend fun deleteMarkersLocally(markers: List<Marker>, aid: String) {
        local.deleteMarkers(markers.map { MarkerMapper.toLocalMarker(it) })
    }

    fun getMarkersSync(scoreId: String): List<Marker> {
        return local.getMarkersSync(userId, scoreId).map { MarkerMapper.fromLocalMarker(it) }
    }

    fun getMarkers(scoreId: String): LiveData<List<Marker>> {
        return Transformations.map(local.getMarkers(userId, scoreId)) {
            it.map { marker -> MarkerMapper.fromLocalMarker(marker) }
        }
    }

    override fun onGetMarkers(markers: List<Marker>) {
        local.deleteMarkers()
        local.saveMarkers(markers.map { MarkerMapper.toLocalMarker(it) })
    }
}
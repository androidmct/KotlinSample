package com.bytepace.dimusco.data.source.local

import androidx.lifecycle.LiveData
import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import com.bytepace.dimusco.data.source.local.model.MarkerDB

object MarkerLocalDataSource {
    private val database: DimuscoDatabase by lazy {
        DimuscoDatabase.getInstance()
    }

    private fun saveMarker(marker: MarkerDB) {
        database.markerDao().insert(marker)
    }

    fun saveMarkers(markers: List<MarkerDB>) {
        markers.forEach { saveMarker(it) }
    }

    fun getMarkersSync(ownerId: String, scoreId: String): List<MarkerDB> {
        return database.markerDao().selectByOwnerIdSync(ownerId, scoreId)
    }

    fun getMarkers(ownerId: String, scoreId: String): LiveData<List<MarkerDB>> {
        return database.markerDao().selectByScoreId(ownerId, scoreId)
    }

    fun getMarkers(ownerId: String): LiveData<List<MarkerDB>> {
        return database.markerDao().selectByOwnerId(ownerId)
    }

    private fun deleteMarker(marker: MarkerDB) {
        database.markerDao().delete(marker)
    }

    fun deleteMarkers(markers: List<MarkerDB>) {
        markers.forEach { deleteMarker(it) }
    }

    fun deleteMarkers() {
        deleteMarkers(database.markerDao().selectAllSync())
    }
}
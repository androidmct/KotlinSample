package com.bytepace.dimusco.data.source.remote

import com.bytepace.dimusco.data.model.Marker
import com.bytepace.dimusco.data.source.remote.socket.SocketMarkersListener
import com.bytepace.dimusco.data.source.remote.socket.SocketService

class MarkerRemoteDataSource(callback: SocketMarkersListener) {
    init {
        SocketService.setMarkerCallback(callback)
    }

    fun createMarker(markers: List<Marker>, aid: String) {
        SocketService.createMarker(markers, aid)
    }
}
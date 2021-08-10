package com.bytepace.dimusco.data.source.remote.socket

import com.bytepace.dimusco.data.model.Marker

interface SocketMarkersListener {
    fun onGetMarkers(markers: List<Marker>)
}
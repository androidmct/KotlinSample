package com.bytepace.dimusco.data.source.remote.socket

import com.bytepace.dimusco.data.model.Layer
import com.bytepace.dimusco.data.model.LayerPage

interface SocketLayersListener {
    fun onGetLayers(layers: List<Layer>)
    fun onGetLayerId(lid: String, token: String)
    fun onGetLayerPage(layerPage: LayerPage)
    fun onLayerChanged(layer: Layer)
    fun onLayerDeleted(laid: String)
}
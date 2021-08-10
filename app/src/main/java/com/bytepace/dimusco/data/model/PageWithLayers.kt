package com.bytepace.dimusco.data.model

data class PageWithLayers constructor(
    val paid: String,
    var page: Page? = null,
    var layerPages: List<LayerPage>? = null
)
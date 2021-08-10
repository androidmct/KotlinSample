package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.model.LayerPage
import com.bytepace.dimusco.data.source.local.model.LayerPageDB
import com.bytepace.dimusco.data.source.local.model.PageWithPictures

object LayerPageMapper {

    fun toLocalLayerPage(layerPage: LayerPage): LayerPageDB {
        return LayerPageDB(
            layerPage.paid, layerPage.layerId, layerPage.width, layerPage.height
        )
    }

    // TODO had to filter pictures from other layers because of missing LayerPageId param
    fun fromLocalLayerPage(layerPage: PageWithPictures): LayerPage {
        return LayerPage(
            layerPage.page.layerId,
            layerPage.page.paid,
            layerPage.page.width,
            layerPage.page.height,
            layerPage.pictures
                .filter { it.picture.layerId == layerPage.page.layerId }
                .map { PictureMapper.fromLocalPicture(it) }
        )
    }
}
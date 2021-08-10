package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.model.Layer
import com.bytepace.dimusco.data.source.local.model.LayerWithPages

object LayerMapper {

    fun toLocalLayer(
        userId: String,
        layer: Layer
    ): com.bytepace.dimusco.data.source.local.model.LayerDB {
        return com.bytepace.dimusco.data.source.local.model.LayerDB(
            layer.laid,
            layer.lid,
            layer.name,
            layer.type,
            layer.version,
            layer.position,
            layer.sid,
            userId,
            layer.createdBy,
            layer.isVisible,
            layer.isImageDrawings,
            layer.isImageSymbols,
            layer.isImageText,
            layer.isActive,
            layer.token
        )
    }

    fun fromLocalLayer(layer: com.bytepace.dimusco.data.source.local.model.LayerDB): Layer {
        return Layer(
            layer.laid,
            layer.lid,
            layer.name,
            layer.type,
            layer.version,
            layer.position,
            layer.scoreId,
            layer.createdBy,
            layer.isVisible,
            layer.isImageDrawings,
            layer.isImageSymbols,
            layer.isImageText,
            layer.isActive,
            layer.token
        )
    }

    fun fromLocalLayer(layer: LayerWithPages): Layer {
        return Layer(
            layer.layer.laid,
            layer.layer.lid,
            layer.layer.name,
            layer.layer.type,
            layer.layer.version,
            layer.layer.position,
            layer.layer.scoreId,
            layer.layer.createdBy,
            layer.layer.isVisible,
            layer.layer.isImageDrawings,
            layer.layer.isImageSymbols,
            layer.layer.isImageText,
            layer.layer.isActive,
            layer.layer.token
        )
    }
}
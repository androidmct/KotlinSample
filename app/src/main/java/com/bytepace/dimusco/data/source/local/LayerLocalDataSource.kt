package com.bytepace.dimusco.data.source.local

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import com.bytepace.dimusco.data.source.local.model.*
import com.bytepace.dimusco.utils.readFile
import com.bytepace.dimusco.utils.saveImage
import com.bytepace.dimusco.utils.saveImageThumbnail

class LayerLocalDataSource {

    private val database: DimuscoDatabase by lazy {
        DimuscoDatabase.getInstance()
    }

    fun getLayers(userId: String, scoreId: String): LiveData<List<LayerDB>> {
        return database.layerDao().select(userId, scoreId)
    }

    fun getLayer(layerId: String): LayerWithPages {
        return database.layerDao().selectByIdWithPages(layerId)
    }

    fun getEditableLayers(layerIds: List<String>): List<LayerWithPages> {
        return database.layerDao().selectEditable(layerIds)
    }

    fun getVisibleLayers(userId: String, scoreId: String): LiveData<List<LayerDB>> {
        return database.layerDao().selectVisibleLayers(userId, scoreId)
    }

    fun getVisibleLayersWithPages(userId: String, scoreId: String): LiveData<List<LayerWithPages>> {
        return database.layerDao().selectVisible(userId, scoreId)
    }

    fun saveLayers(userId: String, layers: List<LayerDB>) {
        database.runInTransaction {
            fixActiveLayers(userId, layers)
            database.layerDao().deleteRedundant(userId, layers.map { it.lid })
            database.layerDao().insert(layers)
        }
    }

    private fun fixActiveLayers(userId: String, layers: List<LayerDB>) {
        val activeLayersIds = database.layerDao().selectActiveLayersIds(userId)
        layers
            .groupBy { layer -> layer.scoreId }
            .map { layerGroup -> layerGroup.value }
            .forEach { layerGroup ->
                val activeLayers =
                    layerGroup.filter { layer -> activeLayersIds.contains(layer.lid) }
                if (activeLayers.isEmpty()) {
                    layerGroup.maxByOrNull { layer -> layer.position }?.isActive = true
                } else {
                    activeLayers.forEach { layer -> layer.isActive = true }
                }
            }
    }

    fun updateLayers(layers: List<LayerDB>) {
        database.layerDao().update(layers)
    }

    fun updateVisibility(layerId: String, isVisible: Boolean) {
        database.layerDao().updateVisibility(layerId, isVisible)
    }

    fun setActive(userId: String, layerId: String, scoreId: String) {
        database.runInTransaction {
            database.layerDao().deselectActiveLayer(userId, scoreId)
            database.layerDao().setActive(layerId)
        }
    }

    fun createLayer(userId: String, layer: LayerDB) {
        database.runInTransaction {
            database.layerDao().deselectActiveLayer(userId, layer.scoreId)
            database.layerDao().insert(layer)
        }
    }

    fun saveLayer(layer: LayerDB) {
        database.runInTransaction {
            val dbLayer = database.layerDao().selectById(layer.lid)
            if (dbLayer != null) {
                layer.isActive = dbLayer.isActive
                database.layerDao().update(layer)
            } else {
                val activeLayersIds = database.layerDao()
                    .selectActiveLayersIds(layer.userId, layer.scoreId)
                if (activeLayersIds.isEmpty()) {
                    layer.isActive = true
                }
                database.layerDao().insert(layer)
            }
        }
    }

    fun updateLayerId(lid: String, token: String) {
        database.runInTransaction {
            val oldLid = database.layerDao().getLayerId(token)
            database.layerDao().updateLayerId(lid, token)
            database.layerPageDao().updateLayerId(oldLid, lid)
            database.pictureDao().updateLayerId(oldLid, lid)
        }
    }

    fun deleteLayer(laid: String): String? {
        var name: String? = null
        database.runInTransaction {
            val layer = database.layerDao().selectByLaid(laid)
            if (layer != null) {
                name = layer.name
                database.layerDao().delete(laid)
                if (layer.isActive) {
                    database.layerDao().setTopLayerActive(layer.userId, layer.scoreId)
                }
            }
        }
        return name
    }

    fun getTopLayerPosition(userId: String, scoreId: String): Int {
        return database.layerDao().getTopLayerPosition(userId, scoreId)
    }

    fun getPages(pageId: String): LiveData<List<PageWithPictures>> {
        return database.layerPageDao().select(pageId)
    }

    fun getPageFile(filename: String): ByteArray? {
        return readFile(filename)
    }

    fun savePageImage(filename: String, bitmap: Bitmap): Throwable? {
        return saveImage(filename, bitmap)
    }

    fun savePageThumbnail(filename: String, bitmap: Bitmap): Throwable? {
        return saveImageThumbnail(filename, bitmap)
    }

    fun saveLayerPage(
        layerPage: LayerPageDB,
        pictures: List<PictureDB>,
        picturesWithPoints: List<PictureWithPoints>
    ) {
        database.runInTransaction {
            database.layerPageDao().insert(layerPage)
            database.pictureDao().delete(layerPage.paid, layerPage.layerId)
            database.pictureDao().insert(pictures)
            for (picture in picturesWithPoints) {
                val pictureId = database.pictureDao().insert(picture.picture)
                picture.points.forEach { it.pictureId = pictureId }
                database.pathPointDao().insert(picture.points)
            }
        }
    }

    fun createPageSync(sync: SyncDB): Long {
        return database.syncDao().insert(sync)
    }

    fun deletePageSync(id: Long) {
        database.syncDao().delete(id)
    }
}
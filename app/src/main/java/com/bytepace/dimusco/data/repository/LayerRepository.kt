package com.bytepace.dimusco.data.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.bytepace.dimusco.data.mapper.LayerMapper
import com.bytepace.dimusco.data.mapper.LayerPageMapper
import com.bytepace.dimusco.data.mapper.PictureMapper
import com.bytepace.dimusco.data.model.Layer
import com.bytepace.dimusco.data.model.LayerPage
import com.bytepace.dimusco.data.source.local.LayerLocalDataSource
import com.bytepace.dimusco.data.source.local.model.PictureWithPoints
import com.bytepace.dimusco.data.source.local.model.SyncDB
import com.bytepace.dimusco.data.source.remote.socket.SocketLayersListener
import com.bytepace.dimusco.data.source.remote.socket.SocketService
import com.bytepace.dimusco.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class LayerRepository private constructor(
    private val userId: String
) : SocketLayersListener {

    companion object {
        private lateinit var instance: LayerRepository

        fun init(userId: String) {
            instance = LayerRepository(userId)
        }

        fun get(): LayerRepository {
            if (::instance.isInitialized) {
                return instance
            }
            throw UninitializedPropertyAccessException(
                "Call init(String) before using this method."
            )
        }

        fun setDeleteLayerCallback(deleteCallback: (String) -> Unit) {
            instance.deleteCallback = deleteCallback
        }

        fun removeDeleteLayerCallback() {
            instance.deleteCallback = null
        }
    }

    private val local = LayerLocalDataSource()
    private val messageBuilder by lazy { SocketMessageBuilder() }

    private var deleteCallback: ((String) -> Unit)? = null

    init {
        SocketService.setLayerCallback(this)
    }

    fun getLayers(scoreId: String): LiveData<List<Layer>> {
        return Transformations.map(local.getLayers(userId, scoreId)) { dbLayers ->
            dbLayers
                .map { LayerMapper.fromLocalLayer(it) }
                .groupBy { it.type == LAYER_TYPE_READ_ONLY }
                .flatMap { it.value }
        }
    }

    fun getVisibleLayers(scoreId: String): LiveData<List<Layer>> {
        return Transformations.map(local.getVisibleLayers(userId, scoreId)) { dbLayers ->
            dbLayers.map { LayerMapper.fromLocalLayer(it) }
        }
    }

    fun getLayerPages(pageId: String): LiveData<List<LayerPage>> {
        return Transformations.map(local.getPages(pageId)) { pages ->
            pages.map { LayerPageMapper.fromLocalLayerPage(it) }
        }
    }

    fun getVisibleLayersPages(scoreId: String): LiveData<List<LayerPage>> {
        return Transformations.map(local.getVisibleLayersWithPages(userId, scoreId)) { dbLayers ->
            dbLayers.flatMap { dbLayer ->
                dbLayer.layerPages.map { LayerPageMapper.fromLocalLayerPage(it) }
            }
        }
    }

    suspend fun updateLayer(layer: Layer) {
        runAsync { local.saveLayer(LayerMapper.toLocalLayer(userId, layer)) }
    }

    suspend fun updateLayers(layers: List<Layer>) {
        runAsync { local.updateLayers(layers.map { LayerMapper.toLocalLayer(userId, it) }) }
    }

    suspend fun getEditableLayers(layerIds: List<String>): List<Layer> {
        return withContext(coroutineContext + Dispatchers.IO) {
            local.getEditableLayers(layerIds).map { LayerMapper.fromLocalLayer(it) }
        }
    }

    suspend fun updateVisibility(layerId: String, isVisible: Boolean) {
        runAsync { local.updateVisibility(layerId, isVisible) }
    }

    suspend fun setActive(layerId: String, scoreId: String) {
        runAsync { local.setActive(userId, layerId, scoreId) }
    }

    suspend fun createLayer(layer: Layer) {
        val position = withContext(Dispatchers.IO) { local.getTopLayerPosition(userId, layer.sid) }

        layer.position = if (position == 0) LAYER_MIN_POS else position + 1

        withContext(Dispatchers.IO) {
            local.createLayer(userId, LayerMapper.toLocalLayer(userId, layer))
        }
    }

    fun getLayerImage(filename: String): Bitmap? {


        local.getPageFile(filename)?.let {
       //     Log.d("byteArraySize"+filename+"=",it.size.toString())

            return getOptimizedImage(it)
        }
        return null
    }

    suspend fun deleteLayer(laid: String) {
        val id = withContext(Dispatchers.IO) {
            local.deleteLayer(laid)
        }
        id?.let { deleteCallback?.invoke(it) }
    }

    fun updateLayerPage(layerPage: LayerPage) {
        local.saveLayerPage(
            LayerPageMapper.toLocalLayerPage(layerPage),
            mapPicturesWithoutPoints(layerPage),
            mapPicturesWithPoints(layerPage)
        )
    }

    private fun mapPicturesWithoutPoints(layerPage: LayerPage): List<com.bytepace.dimusco.data.source.local.model.PictureDB> {
        return layerPage.pictures
            .filter { it.type != PICTURE_TYPE_BRUSH }
            .map {
                if (it.type == PICTURE_TYPE_IMAGE) {
                    val throwable = local.savePageImage(it.filePath, decodeImage(it.imageData))
                    throwable?.printStackTrace()
                }
                PictureMapper.toLocalPicture(
                    layerPage.paid,
                    layerPage.layerId,
                    it,
                    System.currentTimeMillis()
                )
            }
    }

    private fun mapPicturesWithPoints(layerPage: LayerPage): List<PictureWithPoints> {
        return layerPage.pictures
            .filter { it.type == PICTURE_TYPE_BRUSH }
            .map {
                PictureMapper.toPictureWithPoints(
                    layerPage.paid,
                    layerPage.layerId,
                    it,
                    System.currentTimeMillis()
                )
            }
    }

    private suspend fun runAsync(function: () -> Unit) {
        withContext(coroutineContext + Dispatchers.IO) {
            function()
        }
    }

    override fun onGetLayers(layers: List<Layer>) {
        if (layers.isEmpty()) return

        GlobalScope.launch {
            runAsync {
                local.saveLayers(userId, layers.map { LayerMapper.toLocalLayer(userId, it) })
            }
        }
    }

    override fun onGetLayerId(lid: String, token: String) {
        local.updateLayerId(lid, token)
        local.getLayer(lid).layerPages
            .map { LayerPageMapper.fromLocalLayerPage(it) }
            .forEach { layerPage ->
                val refId = "${layerPage.layerId}-${layerPage.paid}"
                val sync = SyncDB(
                    userId,
                    MESSAGE_TYPE_LAYER_PAGE_SYNC,
                    refId,
                    messageBuilder.createPageSyncRequest(layerPage)
                )
                val syncId = local.createPageSync(sync)
                if (SocketService.synchronize(sync)) {
                    local.deletePageSync(syncId)
                }
            }
    }

    override fun onGetLayerPage(layerPage: LayerPage) {
        updateLayerPage(layerPage)
    }

    override fun onLayerChanged(layer: Layer) {
        local.saveLayer(LayerMapper.toLocalLayer(userId, layer))
    }

    override fun onLayerDeleted(laid: String) {
        GlobalScope.launch { deleteLayer(laid) }
    }
}
package com.bytepace.dimusco.data.repository

import androidx.room.withTransaction
import com.bytepace.dimusco.data.model.*
import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import com.bytepace.dimusco.data.source.local.model.SyncDB
import com.bytepace.dimusco.data.source.remote.socket.SocketService
import com.bytepace.dimusco.data.source.remote.socket.SocketSyncListener
import com.bytepace.dimusco.utils.*

class SyncRepository private constructor(private val userId: String) : SocketSyncListener {

    companion object {
        private lateinit var instance: SyncRepository

        fun init(userId: String) {
            instance = SyncRepository(userId)
        }

        fun get(): SyncRepository {
            if (::instance.isInitialized) {
                return instance
            }
            throw UninitializedPropertyAccessException("Call init(String) before using this method.")
        }
    }

    private val builder by lazy { SocketMessageBuilder() }
    private val db by lazy { DimuscoDatabase.getInstance() }

    init {
        SocketService.setSyncCallback(this)
    }

    fun saveSettings(settings: Settings) {
        val message = builder.createSettingsRequest(settings)
        val sync = SyncDB(userId, MESSAGE_TYPE_SETTINGS, userId, message)
        db.runInTransaction {
            db.syncDao().delete(userId, userId)
            synchronize(sync)
        }
    }

    fun createLayer(layer: Layer) {
        val message = builder.createLayerCreateRequest(layer)
        val sync = SyncDB(userId, MESSAGE_TYPE_LAYER_CREATE, layer.lid, message)
        db.runInTransaction {
            db.syncDao().delete(userId, MESSAGE_TYPE_LAYER_CREATE, layer.lid)
            synchronize(sync)
        }
    }

    fun deleteLayer(lid: String, laid: String) {
        db.runInTransaction {
            val isLocal = db.syncDao().delete(userId, MESSAGE_TYPE_LAYER_CREATE, lid) > 0
            db.syncDao().delete(userId, lid)
            if (!isLocal) {
                val message = builder.createLayerDeleteRequest(laid)
                val sync = SyncDB(userId, MESSAGE_TYPE_LAYER_DELETE, "", message)
                synchronize(sync)
            }
        }
    }

    fun updateLayer(layer: Layer) {
        val message = builder.createLayerChangeRequest(layer)
        val sync = SyncDB(userId, MESSAGE_TYPE_LAYER_CHANGE, layer.lid, message)
        db.runInTransaction {
            db.syncDao().delete(userId, MESSAGE_TYPE_LAYER_CHANGE, layer.lid)
            synchronize(sync)
        }
    }

    fun updateLayerPage(layerPage: LayerPage) {
        val refId = "${layerPage.layerId}-${layerPage.paid}"
        val message = builder.createPageSyncRequest(layerPage)
        val sync = SyncDB(userId, MESSAGE_TYPE_LAYER_PAGE_SYNC, refId, message)
        db.runInTransaction {
            db.syncDao().delete(userId, refId)
            synchronize(sync)
        }
    }

    fun createMarker(markers: List<Marker>, aid: String) {
        val message = builder.createMarkerCreateRequest(markers, aid)
        val sync = SyncDB(userId, MESSAGE_TYPE_MARKER, aid, message)
        db.runInTransaction {
            db.syncDao().delete(userId, aid)
            synchronize(sync)
        }
    }

    fun updateTabs(tabs: List<Tab>) {
        val message = builder.createTabsRequest(tabs)
        val refId = StringBuilder().apply { tabs.map { append(it.id) } }.toString()
        val sync = SyncDB(userId, MESSAGE_SYNC_TABS, refId, message)
        db.runInTransaction {
            db.syncDao().delete(userId, refId)
            synchronize(sync)
        }
    }

    suspend fun updateWindows(tabs: List<ScorePagesWindows>) {
        val message = builder.createWindowsRequest(tabs)
        val refId = StringBuilder().apply { tabs.map { append(it.scoreId) } }.toString()
        val sync = SyncDB(userId, MESSAGE_SYNC_TABS, refId, message)
        db.withTransaction {
            db.syncDao().delete(userId, refId)
            db.pageWindowsDao().deleteAll()
            synchronize(sync)
        }
    }

    private fun getSyncs(): List<SyncDB> {
        return db.syncDao().select(userId)
    }

    private fun synchronize(sync: SyncDB) {
        val id = db.syncDao().insert(sync)
        if (SocketService.synchronize(sync)) {
            db.syncDao().delete(id)
        }
    }

    override fun onConnected() {
        getSyncs().forEach { sync ->
            if (SocketService.synchronize(sync)) {
                sync.id?.let { db.syncDao().delete(it) }
            }
        }
    }
}
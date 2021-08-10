package com.bytepace.dimusco.data.source.remote.socket

import androidx.databinding.ObservableBoolean
import androidx.room.withTransaction
import com.bytepace.dimusco.data.mapper.PageWindowMapper
import com.bytepace.dimusco.data.model.*
import com.bytepace.dimusco.data.repository.UserRepository
import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import com.bytepace.dimusco.data.source.local.model.SyncDB
import com.bytepace.dimusco.data.source.remote.api.http.apiClient
import com.bytepace.dimusco.data.source.remote.model.SocketMessage
import com.bytepace.dimusco.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import timber.log.Timber

private const val CON_CLOSE_CODE = 1000
private const val UNINITIALIZED_MESSAGE = "Call connect() at least once before using this method."

class SocketService : WebSocketListener() {

    companion object {

        private val instance by lazy { SocketService() }
        val connectivityFlag = ObservableBoolean()

        private var connectivityCallback: ((Boolean) -> Unit)? = null
        private var connectivityCallbackForDialogs: ((Boolean) -> Unit)? = null

        fun connect(session: Session) {
            instance.apply {   //kotlin apply is an extension function on a type. It runs on the object reference (also known as receiver) into the expression and returns the object reference on completion.
                disconnect()
                socket = apiClient.newWebSocket(createRequest(session.ddid, session.token), this)
            }
        }

        fun requestPages(aid: String) {
            instance.sendMessage(SocketMessageBuilder().createPagesRequest(aid))
        }

        fun requestPages(pages: List<String>) {
            instance.sendMessage(SocketMessageBuilder().createGetPagesRequest(pages))
        }

        fun synchronize(sync: SyncDB): Boolean {
            return instance.sendMessage(sync.message)
        }

        fun createMarker(markers: List<Marker>, aid: String) {
            instance.sendMessage(SocketMessageBuilder().createMarkerCreateRequest(markers, aid))
        }

        fun disconnect() {
            instance.isConnected = false
            if (instance::socket.isInitialized) {
                instance.socket.close(CON_CLOSE_CODE, null)
            }
        }

        fun setSettingsCallback(callback: SocketSettingsListener) {
            instance.settingsCallback = callback
        }

        fun setScoreCallback(callback: SocketScoreListener) {
            instance.scoreCallback = callback
        }

        fun setLayerCallback(callback: SocketLayersListener) {
            instance.layersCallback = callback
        }

        fun setSyncCallback(callback: SocketSyncListener) {
            instance.syncCallback = callback
        }

        fun setMarkerCallback(callback: SocketMarkersListener) {
            instance.markersCallback = callback
        }

        fun setConnectivityCallback(connectivityCallback: ((Boolean) -> Unit)) {
            this.connectivityCallback = connectivityCallback
        }

        fun setConnectivityCallbackForDialogs(connectivityCallback: ((Boolean) -> Unit)) {
            this.connectivityCallbackForDialogs = connectivityCallback
        }

        fun setTabsCallback(callback: SocketTabsListener) {
            instance.tabsCallback = callback
        }
    }

    private val gson = Gson()
    private val scorePageTemp = mutableMapOf<String?, String?>()
    private var isConnected = false
    private val database by lazy { DimuscoDatabase.getInstance() }


    private var syncCallback: SocketSyncListener? = null
    private var scoreCallback: SocketScoreListener? = null
    private var settingsCallback: SocketSettingsListener? = null
    private var layersCallback: SocketLayersListener? = null
    private var markersCallback: SocketMarkersListener? = null
    private var tabsCallback: SocketTabsListener? = null

    private lateinit var socket: WebSocket

    private fun createRequest(ddid: Int, token: String): Request {
        return Request.Builder().url("$SOCKET_ENDPOINT$ddid/$token").build()
    }

    private fun sendMessage(message: String): Boolean {
        if (::socket.isInitialized) {
            socket.send(message)
            return isConnected
        } else {
            throw UninitializedPropertyAccessException(UNINITIALIZED_MESSAGE)
        }
    }

    private fun parseMessage(message: SocketMessage) {
//        Timber.e("MESSAGE $message")
        if (message.lid != null && message.token != null) {
            parseLayerIdMessage(message)
            return
        }
        if (message.md5 != null) {
            parseScorePage(message)
            return
        }
        when (message.type) {
            MESSAGE_TYPE_LAYERS -> parseLayersMessage(message)
            MESSAGE_TYPE_LAYER_CHANGE -> parseChangeLayerMessage(message)
            MESSAGE_TYPE_LAYER_CREATE -> parseCreateLayerMessage(message)
            MESSAGE_TYPE_LAYER_DELETE -> parseDeleteLayerMessage(message)
            MESSAGE_TYPE_LAYER_PAGE_SYNC -> parseSyncLayerPageMessage(message)
            MESSAGE_TYPE_MARKER -> parseMarkerMessage(message)
            MESSAGE_TYPE_SETTINGS -> parseSettingsMessage(message)
            MESSAGE_TYPE_SCORE -> parseScoreMessage(message)
            MESSAGE_SYNC_TABS -> parseTabs(message)
            MESSAGE_TYPE_WINDOW_SYNC -> parseWindows(message)
        }
    }

    private fun parseWindows(message: SocketMessage) {
        val data = message.data as Map<String, String>
        val type = object : TypeToken<List<ScorePagesWindows>>() {}.type
        val windows: List<ScorePagesWindows> = gson.fromJson(data["data"], type)
        GlobalScope.launch(Dispatchers.IO) {
            database.withTransaction {
                database.pageWindowsDao().deleteAll()
                database.pageWindowsDao().insert(*PageWindowMapper.map(windows).toTypedArray())
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseTabs(message: SocketMessage) {
        val messageData = message.data as Map<String, String>
        val type = object : TypeToken<List<Tab>>() {}.type
        val tabs = gson.fromJson<List<Tab>>(messageData["data"], type)
        Timber.d("DATA tabs: $tabs")
        tabsCallback?.saveTabs(tabs)
    }

    private fun parseLayerIdMessage(message: SocketMessage) {
        message.lid?.let { lid ->
            message.token?.let { token ->
                Timber.d("DATA layerId: $lid token: $token")
                layersCallback?.onGetLayerId(lid, token)
            }
        }
    }

    private fun parseLayersMessage(message: SocketMessage) {
        val type = object : TypeToken<List<Layer>>() {}.type
        val layers = gson.fromJson<List<Layer>>(gson.toJson(message.data), type)
        Timber.d("DATA layers: $layers")
        layersCallback?.onGetLayers(layers)
    }

    private fun parseChangeLayerMessage(message: SocketMessage) {
        val layer = gson.fromJson(gson.toJson(message.data), Layer::class.java)
        Timber.d("DATA change layer: $layer")
        layersCallback?.onLayerChanged(layer)
    }

    private fun parseCreateLayerMessage(message: SocketMessage) {
        val layer = gson.fromJson(gson.toJson(message.data), Layer::class.java)
        Timber.d("DATA create layer: $layer")
        layersCallback?.onLayerChanged(layer)
    }

    private fun parseDeleteLayerMessage(message: SocketMessage) {
        val laid = message.laid ?: return
        Timber.d("DATA layer delete: $laid")
        layersCallback?.onLayerDeleted(laid)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseSyncLayerPageMessage(message: SocketMessage) {
        val messageData = message.data as Map<String, String>
        val layerPage = gson.fromJson(messageData["data"], LayerPage::class.java)
        Timber.d("DATA sync layerPage: $layerPage")
        layersCallback?.onGetLayerPage(layerPage)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseSettingsMessage(message: SocketMessage) {
        val messageData = message.data as Map<String, String>
        val settings = gson.fromJson(messageData["data"], Settings::class.java)
        Timber.d("DATA settings: $settings")
        settingsCallback?.onGetSettings(settings)
    }

    private fun parseScoreMessage(message: SocketMessage) {
        val type = object : TypeToken<List<Score>>() {}.type
        val scores = gson.fromJson<List<Score>>(gson.toJson(message.data), type)
        scoreCallback?.onGetScores(scores)
    }

    private fun parseScorePage(message: SocketMessage) {
        Timber.d("DATA score page: ${message.md5} ${message.data}")
        scorePageTemp[message.md5] = message.name
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseMarkerMessage(message: SocketMessage) {
        val data = (message.data as ArrayList<String>)
        val markers = ArrayList<Marker>()
        val type = object : TypeToken<List<Marker>>() {}.type
        data.forEach {
            val parsedMakers = gson.fromJson<List<Marker>>(it, type)
            Timber.d("DATA marker: $parsedMakers")
            markers.addAll(parsedMakers)
        }
        markersCallback?.onGetMarkers(markers)
    }

    private fun checkPageScore(bytes: ByteString) {
        val md5 = getMd5(bytes)
        scorePageTemp[md5]?.let {
            scorePageTemp.remove(md5)
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        isConnected = true
        connectivityFlag.set(true)
        syncCallback?.onConnected()
        notify(true)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        notify(true)
        try {
            val response = gson.fromJson(text, SocketMessage::class.java)
            parseMessage(response)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        checkPageScore(bytes)
        notify(true)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        isConnected = false
        notify(false)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        isConnected = false
        connectivityFlag.set(false)
        notify(false)
        reconnect()
        t.printStackTrace()
    }

    private fun reconnect() {
        UserRepository.get().getSession()?.let {
            connect(it)
            connectivityFlag.set(true)
        }
    }

    private fun notify(isConnected: Boolean) {
        GlobalScope.launch(context = Dispatchers.Main) {
            connectivityCallback?.invoke(isConnected)
            connectivityCallbackForDialogs?.invoke(isConnected)
        }
    }
}
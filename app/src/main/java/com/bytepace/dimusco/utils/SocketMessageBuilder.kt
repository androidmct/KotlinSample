package com.bytepace.dimusco.utils

import com.bytepace.dimusco.data.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject

private const val KEY_TYPE = "type"
private const val KEY_AID = "aid"
private const val KEY_DATA = "data"
private const val KEY_LAID = "laid"
private const val KEY_LID = "lid"
private const val KEY_PAID = "paid"
private const val KEY_SCORE_ID = "scoreId"
private const val KEY_PAGE_TO = "pageTo"
private const val KEY_COLOR = "color"
private const val KEY_OWNER_ID = "ownerId"
private const val KEY_IS_ACTIVE = "isActive"
private const val KEY_CREATED_BY = "created_by"
private const val KEY_LAYER_PAGES = "layer_pages"
private const val KEY_PAGES = "pages"

class SocketMessageBuilder {

    private val gson = Gson()

    fun createPagesRequest(aid: String): String {
        val map = HashMap<String, String>()
        map[KEY_TYPE] = MESSAGE_TYPE_DOWNLOAD
        map[KEY_AID] = aid
        return gson.toJson(map)
    }

    fun createSettingsRequest(settings: Settings): String {
        val map = HashMap<String, Any>()
        map[KEY_TYPE] = MESSAGE_TYPE_SETTINGS
        map[KEY_DATA] = gson.toJson(settings)
        return gson.toJson(map)
    }

    fun createLayerChangeRequest(layer: Layer): String {
        val jsonLayer = (gson.toJsonTree(layer) as JsonObject).apply {
            remove(KEY_IS_ACTIVE)
            remove(KEY_CREATED_BY)
            remove(KEY_TYPE)
        }
        val map = HashMap<String, Any>()
        map[KEY_TYPE] = MESSAGE_TYPE_LAYER_CHANGE
        map[KEY_DATA] = jsonLayer
        return gson.toJson(map)
    }

    fun createLayerCreateRequest(layer: Layer): String {
        val jsonLayer = (gson.toJsonTree(layer) as JsonObject).apply {
            remove(KEY_IS_ACTIVE)
            remove(KEY_LAYER_PAGES)
            remove(KEY_LID)
            remove(KEY_LAID)
            remove(KEY_CREATED_BY)
            remove(KEY_TYPE)
        }
        val map = HashMap<String, Any>()
        map[KEY_TYPE] = MESSAGE_TYPE_LAYER_CREATE
        map[KEY_DATA] = jsonLayer
        return gson.toJson(map)
    }

    fun createLayerDeleteRequest(laid: String): String {
        val data = HashMap<String, String>()
        data[KEY_LAID] = laid
        val map = HashMap<String, Any>()
        map[KEY_TYPE] = MESSAGE_TYPE_LAYER_DELETE
        map[KEY_DATA] = data
        return gson.toJson(map)
    }

    fun createPageSyncRequest(page: LayerPage): String {
        val data = HashMap<String, Any>()
        data[KEY_LID] = page.layerId
        data[KEY_PAID] = "P${page.paid.substringAfter('P').substringBefore('.')}"
        data[KEY_DATA] = gson.toJson(page)
        val map = HashMap<String, Any>()
        map[KEY_TYPE] = MESSAGE_TYPE_LAYER_PAGE_SYNC
        map[KEY_DATA] = data
        return gson.toJson(map)
    }

    fun createMarkerCreateRequest(markers: List<Marker>, aid: String): String {
        val dataArray: Array<HashMap<String, String>> =
            Array(markers.size) { HashMap() }
        for (index in markers.indices) {
            val marker = markers[index]
            val data = HashMap<String, String>()
            data[KEY_SCORE_ID] = marker.scoreId
            data[KEY_PAGE_TO] = marker.pageTo.toString()
            data[KEY_COLOR] = marker.color.toString()
            data[KEY_OWNER_ID] = marker.ownerId

            dataArray[index] = data
        }
        val map = HashMap<String, Any>()
        map[KEY_TYPE] = MESSAGE_TYPE_MARKER
        map[KEY_DATA] = dataArray
        map[KEY_AID] = aid
        return gson.toJson(map)
    }

    fun createGetPagesRequest(pages: List<String>): String {
        val map = HashMap<String, Any>()
        map[KEY_TYPE] = MESSAGE_TYPE_DOWNLOAD_PAGE
        map[KEY_PAGES] = pages
        return gson.toJson(map)
    }

    fun createTabsRequest(tabs: List<Tab>): String {
        val map = HashMap<String, Any>()
        map[KEY_TYPE] = MESSAGE_SYNC_TABS
        map[KEY_DATA] = gson.toJson(tabs)
        return gson.toJson(map)
    }

    fun createWindowsRequest(tabs: List<ScorePagesWindows>): String {
        val map = HashMap<String, Any>()
        map[KEY_TYPE] = MESSAGE_TYPE_WINDOW_SYNC
        map[KEY_DATA] = gson.toJson(tabs)
        return gson.toJson(map)
    }
}
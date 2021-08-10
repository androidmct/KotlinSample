package com.bytepace.dimusco.data.source.remote.model

import com.bytepace.dimusco.data.model.Tab
import com.bytepace.dimusco.utils.MESSAGE_SYNC_TABS

data class SyncTabs(val data: List<Tab> = emptyList()){
    val type: String = MESSAGE_SYNC_TABS
}
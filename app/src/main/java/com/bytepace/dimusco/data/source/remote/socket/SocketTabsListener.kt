package com.bytepace.dimusco.data.source.remote.socket

import com.bytepace.dimusco.data.model.Tab


interface SocketTabsListener {
    fun saveTabs(tabs: List<Tab>)
}
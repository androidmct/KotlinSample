package com.bytepace.dimusco.data.source.remote.socket

import com.bytepace.dimusco.data.model.Settings

interface SocketSettingsListener {
    fun onGetSettings(settings: Settings)
}
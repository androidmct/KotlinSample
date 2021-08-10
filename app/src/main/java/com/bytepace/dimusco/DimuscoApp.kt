package com.bytepace.dimusco

import android.app.Application
import android.content.Context
import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import com.bytepace.dimusco.data.source.local.prefs.PrefsHelper
import com.bytepace.dimusco.service.ConnectivityService
import timber.log.Timber

class DimuscoApp : Application() {

    companion object {
        private lateinit var instance: DimuscoApp

        fun getContext(): Context {
            if (::instance.isInitialized) {
                return instance.applicationContext
            }
            throw UninitializedPropertyAccessException()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        PrefsHelper.init(applicationContext)
        DimuscoDatabase.init(applicationContext)
        ConnectivityService.init(applicationContext)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}
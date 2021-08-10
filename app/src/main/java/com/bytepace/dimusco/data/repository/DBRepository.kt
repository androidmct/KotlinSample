package com.bytepace.dimusco.data.repository

import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DBRepository {

    suspend fun clearDB() {
        withContext(Dispatchers.IO) {
            with(DimuscoDatabase.getInstance()) {
                userDao().clearTable()
                scoreDao().clearTable()
                pageDao().clearTable()
                settingsDao().clearTable()
                colorDao().clearTable()
                symbolDao().clearTable()
                layerDao().clearTable()
                layerPageDao().clearTable()
                pictureDao().clearTable()
                pathPointDao().clearTable()
                syncDao().clearTable()
                markerDao().clearTable()
                tabDao().clearTable()
            }
        }
    }
}
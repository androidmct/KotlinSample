package com.bytepace.dimusco.data.source.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bytepace.dimusco.data.source.local.model.LayerPageDB
import com.bytepace.dimusco.data.source.local.model.PageWithPictures

@Dao
interface LayerPageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(page: LayerPageDB)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(pages: List<LayerPageDB>)

    @Transaction
    @Query("SELECT * FROM layerpage WHERE paid = :pageId")
    fun select(pageId: String): LiveData<List<PageWithPictures>>

    @Query("UPDATE layerpage SET layerId = :newId WHERE layerId = :oldId")
    fun updateLayerId(oldId: String, newId: String)

    @Query("DELETE FROM layerpage")
    suspend fun clearTable()
}
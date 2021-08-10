package com.bytepace.dimusco.data.source.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bytepace.dimusco.data.source.local.model.LayerDB
import com.bytepace.dimusco.data.source.local.model.LayerWithPages
import com.bytepace.dimusco.utils.LAYER_TYPE_READ_WRITE

@Dao
interface LayerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(layers: List<LayerDB>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(layer: LayerDB)

    @Transaction
    @Query("SELECT * FROM layer WHERE scoreId = :scoreId AND userId = :userId ORDER BY position DESC")
    fun select(userId: String, scoreId: String): LiveData<List<LayerDB>>

    @Query("SELECT * FROM layer WHERE scoreId = :scoreId AND userId = :userId AND isVisible = 1")
    fun selectVisibleLayers(userId: String, scoreId: String): LiveData<List<LayerDB>>

    // use ascending ordering for correct layers order on score details screen
    @Transaction
    @Query("SELECT * FROM layer WHERE scoreId = :scoreId AND userId = :userId AND isVisible = 1 ORDER BY position ASC")
    fun selectVisible(userId: String, scoreId: String): LiveData<List<LayerWithPages>>

    @Transaction
    @Query("SELECT * FROM layer WHERE lid IN (:layerIds)")
    fun select(layerIds: List<String>): List<LayerWithPages>

    @Transaction
    @Query("SELECT * FROM layer WHERE lid = :layerId")
    fun selectByIdWithPages(layerId: String): LayerWithPages

    @Transaction
    @Query("SELECT * FROM layer WHERE lid IN (:layerIds) AND type = $LAYER_TYPE_READ_WRITE")
    fun selectEditable(layerIds: List<String>): List<LayerWithPages>

    @Query("SELECT * FROM layer WHERE lid = :layerId")
    fun selectById(layerId: String): LayerDB?

    @Query("SELECT * FROM layer WHERE laid = :laid")
    fun selectByLaid(laid: String): LayerDB?

    @Query("SELECT lid FROM layer WHERE userId = :userId AND isActive = 1")
    fun selectActiveLayersIds(userId: String): List<String>

    @Query("SELECT lid FROM layer WHERE userId = :userId AND scoreId = :scoreId AND isActive = 1")
    fun selectActiveLayersIds(userId: String, scoreId: String): List<String>

    @Query("SELECT MAX(position) FROM layer WHERE scoreId = :scoreId AND userId = :userId")
    fun getTopLayerPosition(userId: String, scoreId: String): Int

    @Query("SELECT lid FROM layer WHERE token = :token")
    fun getLayerId(token: String): String

    @Update
    fun update(layers: List<LayerDB>)

    @Update
    fun update(layer: LayerDB)

    @Query("UPDATE layer SET lid = :lid, token = null WHERE token = :token")
    fun updateLayerId(lid: String, token: String)

    @Query("DELETE FROM layer WHERE laid = :laid")
    fun delete(laid: String)

    @Query("DELETE FROM layer WHERE userId = :userId AND lid NOT IN (:layerIds)")
    fun deleteRedundant(userId: String, layerIds: List<String>)

    @Query("UPDATE layer SET name = :name WHERE lid = :layerId")
    fun updateName(layerId: String, name: String)

    @Query("UPDATE layer SET isVisible = :isVisible WHERE lid = :layerId")
    fun updateVisibility(layerId: String, isVisible: Boolean)

    @Query("UPDATE layer SET isActive = 0 WHERE userId = :userId AND scoreId = :scoreId")
    fun deselectActiveLayer(userId: String, scoreId: String)

    @Query("UPDATE layer SET isActive = 1, isVisible = 1 WHERE lid = :layerId")
    fun setActive(layerId: String)

    @Query("UPDATE layer SET isActive = 1 WHERE userId = :userId AND scoreId = :scoreId AND position = (SELECT MAX(position) FROM layer WHERE userId = :userId AND scoreId = :scoreId)")
    fun setTopLayerActive(userId: String, scoreId: String): Int

    @Query("DELETE FROM layer")
    suspend fun clearTable()
}
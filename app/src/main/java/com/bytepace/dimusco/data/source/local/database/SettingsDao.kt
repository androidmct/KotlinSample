package com.bytepace.dimusco.data.source.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bytepace.dimusco.data.source.local.model.SettingsDB
import com.bytepace.dimusco.data.source.local.model.SettingsWithColorsSymbols

@Dao
interface SettingsDao {

    @Transaction
    @Query("SELECT * FROM settings WHERE userId = :userId")
    fun select(userId: String): LiveData<SettingsWithColorsSymbols>

    @Transaction
    @Query("SELECT * FROM settings WHERE userId = :userId")
    fun selectSync(userId: String): SettingsWithColorsSymbols

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(settings: SettingsDB)

    @Update
    fun update(settings: SettingsDB)

    @Query("UPDATE settings SET pageScrollingHorizontal = :isHorizontal WHERE userId = :userId")
    fun updateScrollDirection(userId: String, isHorizontal: Boolean)


    @Query("UPDATE settings SET confirmSavingChanges = :confirm WHERE userId = :userId")
    fun updateConfirmSavingChanges(userId: String, confirm: Boolean)


    @Query("UPDATE settings SET transparency = :transparency WHERE userId = :userId")
    fun updateTransparency(userId: String, transparency: Float)


    @Query("UPDATE settings SET lineThickness = :thickness WHERE userId = :userId")
    fun updateLineThickness(userId: String, thickness: Float)


    @Query("UPDATE settings SET eraserThickness = :thickness WHERE userId = :userId")
    fun updateEraserThickness(userId: String, thickness: Float)


    @Query("UPDATE settings SET textSize = :textSize WHERE userId = :userId")
    fun updateTextSize(userId: String, textSize: Float)


    @Query("UPDATE settings SET selectedColor = :color WHERE userId = :userId")
    fun updateSelectedColor(userId: String, color: Int)


    @Query("UPDATE settings SET isImageDrawings = :isImage WHERE userId = :userId")
    fun updateIsImageDrawings(userId: String, isImage: Boolean)


    @Query("UPDATE settings SET isImageSymbols = :isImage WHERE userId = :userId")
    fun updateIsImageSymbols(userId: String, isImage: Boolean)


    @Query("UPDATE settings SET isImageText = :isImage WHERE userId = :userId")
    fun updateIsImageText(userId: String, isImage: Boolean)


    @Query("UPDATE settings SET editTimeout = :milliseconds WHERE userId = :userId")
    fun updateEditTimeout(userId: String, milliseconds: Long)

    @Query("DELETE FROM settings")
    suspend fun clearTable()
}
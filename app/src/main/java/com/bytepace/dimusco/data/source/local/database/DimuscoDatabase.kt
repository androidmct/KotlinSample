package com.bytepace.dimusco.data.source.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bytepace.dimusco.data.repository.convertors.ListStringConverter
import com.bytepace.dimusco.data.source.local.model.*

const val DB_NAME = "dimusconew_db"

@Database(
    entities = [
        ScoreDB::class,
        UserDB::class,
        PageDB::class,
        SettingsDB::class,
        ColorDB::class,
        SymbolDB::class,
        LayerDB::class,
        LayerPageDB::class,
        PictureDB::class,
        PathPointDB::class,
        SyncDB::class,
        MarkerDB::class,
        TabDB::class,
        ScorePageWindowDB::class
    ],
    exportSchema = false,
    version = 6
)
@TypeConverters(ListStringConverter::class)
abstract class DimuscoDatabase : RoomDatabase() {

    companion object {
        private lateinit var instance: DimuscoDatabase

        fun getInstance(): DimuscoDatabase {
            if (::instance.isInitialized) {
                return instance
            }

            throw UninitializedPropertyAccessException(
                "Call init(Context) before using this method."
            )
        }

        fun init(context: Context) = synchronized(this) {
            if (::instance.isInitialized) {
                return@synchronized
            }
            instance = Room.databaseBuilder(context, DimuscoDatabase::class.java, DB_NAME).build()
        }
    }

    abstract fun layerDao(): LayerDao

    abstract fun layerPageDao(): LayerPageDao

    abstract fun pageDao(): PageDao

    abstract fun pathPointDao(): PathPointDao

    abstract fun pictureDao(): PictureDao

    abstract fun settingsDao(): SettingsDao

    abstract fun scoreDao(): ScoreDao

    abstract fun userDao(): UserDao

    abstract fun colorDao(): ColorDao

    abstract fun symbolDao(): SymbolDao

    abstract fun syncDao(): SyncDao

    abstract fun markerDao(): MarkerDao

    abstract fun tabDao(): TabDao

    abstract fun pageWindowsDao(): PageWindowsDao
}
package com.bytepace.dimusco.data.source.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bytepace.dimusco.data.source.local.model.UserDB

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE email = :email")
    fun select(email: String): UserDB?

    @Query("SELECT * FROM user WHERE uid = :uid")
    fun selectById(uid: String): UserDB?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserDB)

    @Query("DELETE FROM user WHERE email LIKE :email")
    suspend fun deleteById(email: String)

    @Query("DELETE FROM user")
    suspend fun clearTable()
}
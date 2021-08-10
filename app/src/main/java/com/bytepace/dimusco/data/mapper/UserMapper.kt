package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.model.User

object UserMapper {

    fun toLocalUser(
        ddid: Int,
        token: String,
        user: User,
        password: String
    ): com.bytepace.dimusco.data.source.local.model.UserDB {
        return com.bytepace.dimusco.data.source.local.model.UserDB(
            user.uid,
            ddid,
            token,
            user.email,
            user.name,
            password,
            user.state,
            user.created,
            user.settingsVersion,
            user.libraryVersion
        )
    }

    fun fromLocalUser(user: com.bytepace.dimusco.data.source.local.model.UserDB): User {
        return User(
            user.uid,
            user.email,
            user.name,
            user.state,
            user.created,
            user.settingsVersion,
            user.libraryVersion
        )
    }
    
}
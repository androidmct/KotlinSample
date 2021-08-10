package com.bytepace.dimusco.data.model

data class EditorAction(
    val type: Type,
    val picture: Picture? = null,
    val index: Int? = null,
    val filepath: String? = null
) {
    enum class Type {
        ADD, EDIT, REMOVE, IMAGE
    }
}
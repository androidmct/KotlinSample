package com.bytepace.dimusco.data.model

data class Language(
    val alias: String,
    val name: String,
    val native: String
) {
    override fun toString(): String {
        return "$native - $name"
    }
}
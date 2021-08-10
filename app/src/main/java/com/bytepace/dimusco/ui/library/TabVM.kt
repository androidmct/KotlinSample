package com.bytepace.dimusco.ui.library

data class TabVM(
    val id: String,
    val name: String,
    val order: Long,
    var isChosen: Boolean,
    var scores: MutableList<String>?
)
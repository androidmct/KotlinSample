package com.bytepace.dimusco.ui.components.list

interface OnItemClickListener<T> {

    fun onItemClick(item: T, position: Int)
}
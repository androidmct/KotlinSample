package com.bytepace.dimusco.utils.list

import androidx.recyclerview.widget.DiffUtil

class SelectableDiffCallback : DiffUtil.Callback() {
    private var oldItems: List<SelectableItem> = listOf()
    private var newItems: List<SelectableItem> = listOf()

    fun setItems(oldItems: List<SelectableItem>, newItems: List<SelectableItem>) {
        this.oldItems = oldItems
        this.newItems = newItems
    }

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].value == newItems[newItemPosition].value &&
                oldItems[oldItemPosition].isSelected == newItems[newItemPosition].isSelected
    }
}
package com.bytepace.dimusco.ui.base

import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.ui.components.list.BaseViewHolder

abstract class BaseAdapter<T, VH : BaseViewHolder<T>> : RecyclerView.Adapter<VH>() {

    protected val items = mutableListOf<T>()
    protected var selectedItem: Int? = null

    fun setItems(items: List<T>) {
        this.items.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @JvmName("setSelectedItem1")
    fun setSelectedItem(position: Int?) {
        val oldSelectedItem = selectedItem
        selectedItem = position

        oldSelectedItem?.let { notifyItemChanged(it) }
        selectedItem?.let { notifyItemChanged(it) }
    }

}
package com.bytepace.dimusco.ui.score

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bytepace.dimusco.R
import com.bytepace.dimusco.ui.components.list.adapter.BaseRecyclerViewAdapter
import com.bytepace.dimusco.ui.score.selector.adapter.PageItem

class PageAdapter() : BaseRecyclerViewAdapter<PageItem, PageItemVH>(null) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageItemVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_page_v2, parent, false)
        return PageItemVH(view)
    }
}
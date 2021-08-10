package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.model.Tab

object TabMapper {

    fun toLocalTab(tab: Tab, userId: String): com.bytepace.dimusco.data.source.local.model.TabDB {
        return com.bytepace.dimusco.data.source.local.model.TabDB(
            tab.id,
            tab.name,
            userId,
            tab.order,
            tab.scoresIds,
            false
        )
    }

    fun fromLocalTab(tab: com.bytepace.dimusco.data.source.local.model.TabDB): Tab {
        return Tab(tab.tid, tab.order, tab.name, tab.scoreIds ?: mutableListOf())
    }

}
package com.bytepace.dimusco.data.mapper

import com.bytepace.dimusco.data.model.Page
import com.bytepace.dimusco.data.source.local.model.PageDB

object PageMapper {

    fun toLocalPage(
        page: Page,  //Page is from mode.Page
        aid: String
    ): PageDB {
        return PageDB(page.id.replace(".png", ""), aid, page.pageNumber, page.isDownloaded, page.id)
    }

    fun fromLocalPage(page: PageDB): Page {
        return Page(page.filename, page.pageNumber, page.isDownloaded, page.aid)
    }
}
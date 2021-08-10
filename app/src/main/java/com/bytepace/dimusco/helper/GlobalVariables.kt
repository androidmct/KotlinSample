package com.bytepace.dimusco.helper

import com.bytepace.dimusco.Model.*
import com.bytepace.dimusco.data.model.Score
import com.bytepace.dimusco.data.source.local.model.ComplexPage
import com.bytepace.dimusco.ui.score.selector.adapter.PageItem

class GlobalVariables {
    companion object{
        val tabList: ArrayList<String> = arrayListOf()
        var tabListCount: Int = 0
        var tabListSelectedItem: Int = 0
        var scoreList: ArrayList<Score> = arrayListOf()
        var layerList: ArrayList<LayerModel> = arrayListOf()
        var itemSelectedLayer: Int = 0
        var symbols: ArrayList<SymbolModel> = arrayListOf()
        var colors: ArrayList<ColorModel> = arrayListOf()
        var pages: ArrayList<PageItem> = arrayListOf()
        var scorepages: ArrayList<PageItem> = arrayListOf()
        var miniPages: ArrayList<PageMiniModel> = arrayListOf()

        var symbolActivity: Int = 0

        var cvScreenOrientation: Int = 0

        var mStatus: Boolean = false
        var symbol: String = ""
    }
}
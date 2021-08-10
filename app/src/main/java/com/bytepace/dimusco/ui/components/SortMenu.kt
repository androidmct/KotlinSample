package com.bytepace.dimusco.ui.components

import android.content.Context
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.bytepace.dimusco.data.repository.score.Sorting
import com.bytepace.dimusco.utils.getTranslatedString

class SortMenu(
    private val context: Context,
    anchor: View,
    private val selection: Sorting,
    onSelect: (Sorting) -> Unit
) : PopupMenu(context, anchor, Gravity.END) {

    init {
        addSorting(Sorting.NAME_ASC, onSelect)
        addSorting(Sorting.NAME_DESC, onSelect)
        addSorting(Sorting.COMPOSER_ASC, onSelect)
        addSorting(Sorting.COMPOSER_DESC, onSelect)
    }

    private fun addSorting(sorting: Sorting, callback: (Sorting) -> Unit) {
        menu.add(getTranslatedString(context, sorting.label)).apply {
            isEnabled = sorting != selection
            setOnMenuItemClickListener {
                callback(sorting)
                true
            }
        }
    }
}
package com.bytepace.dimusco.ui.components

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import com.bytepace.dimusco.data.model.Language
import java.util.*

class LanguageMenu(
    context: Context,
    anchor: View,
    languages: SortedMap<String, Language>,
    selection: String?,
    onSelect: (String) -> Unit
) : PopupMenu(context, anchor, Gravity.END) {

    init {
        menu.apply {
            for (language in languages) {
                add(language.value.toString()).apply {
                    isEnabled = language.key != selection
                    setOnMenuItemClickListener {
                        onSelect(language.key)
                        true
                    }
                }
            }
        }
    }
}
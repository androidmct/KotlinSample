package com.bytepace.dimusco.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytepace.dimusco.data.model.Page

class EditorViewModelFactory(
    private val sid: String,
    private val aid: String,
    private val page: Page
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
//            return EditorViewModel(sid, aid, page) as T
//        }
        throw IllegalAccessException()
    }
}
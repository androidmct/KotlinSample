package com.bytepace.dimusco.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bytepace.dimusco.data.model.Page

class ScoreViewModelFactory(
    private val sid: String,
    private val aid: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
            return ScoreViewModel(sid, aid) as T
        }
        throw IllegalAccessException()
    }
}
package com.bytepace.dimusco.ui.components.drawing

import android.graphics.Bitmap
import com.bytepace.dimusco.data.model.EditorAction
import com.bytepace.dimusco.data.model.Picture

interface DrawingViewCallback {
    fun onRequiredImage(filename: String): Bitmap?
    fun onPictureSelected(picture: Picture)
    fun onPictureDeselected()
    fun onEditorAction(action: EditorAction)
}
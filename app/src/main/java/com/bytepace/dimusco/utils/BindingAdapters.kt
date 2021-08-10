package com.bytepace.dimusco.utils

import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView


@BindingAdapter("passwordVisible")
fun EditText.setPasswordVisible(visible: Boolean) {
    val selectionStart = selectionStart
    val selectionEnd = selectionEnd

    inputType = InputType.TYPE_CLASS_TEXT or if (visible) {
        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    } else {
        InputType.TYPE_TEXT_VARIATION_PASSWORD
    }
    typeface = Typeface.DEFAULT

    setSelection(selectionStart, selectionEnd)
}

@BindingAdapter("text")
fun TextView.setTranslatedText(key: String) {
    text = getTranslatedString(context, key)
}

@BindingAdapter("hint")
fun EditText.setTranslatedHint(key: String) {
    hint = getTranslatedString(context, key)
}

@BindingAdapter("contentDescription")
fun ImageView.setTranslatedDescription(key: String) {
    contentDescription = getTranslatedString(context, key)
}

@BindingAdapter("bitmap")
fun ImageView.setSourceBitmap(bitmap: Bitmap?) {
    bitmap?.let { setImageBitmap(it) }
}

@BindingAdapter("colorSrc")
fun ImageView.setColorSource(color: Int) {
    setImageDrawable(ColorDrawable(getOpaqueColor(color)))
}

@BindingAdapter("activated")
fun View.bindActivated(isActivated: Boolean) {
    setActivated(isActivated)
}

@BindingAdapter("clipToOutline")
fun View.bindClipToOutline(clipToOutline: Boolean) {
    this.clipToOutline = clipToOutline
}

@BindingAdapter("scale")
fun View.setScale(scale: Float) {
    scaleX = scale
    scaleY = scale
}

@BindingAdapter("onStopTrackingTouch")
fun SeekBar.setOnStopTrackingTouch(callback: (Int) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            callback(seekBar?.progress ?: 0)
        }

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }
    })
}


@BindingAdapter("bind:scrollTo")
fun scrollTo(recyclerView: RecyclerView, position: Int) {
    recyclerView.scrollToPosition(position)
}
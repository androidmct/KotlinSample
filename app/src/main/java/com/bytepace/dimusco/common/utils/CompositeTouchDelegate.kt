package com.bytepace.dimusco.common.utils

import android.graphics.Rect
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View

class CompositeTouchDelegate(view: View) : TouchDelegate(Rect(), view) {

    private val _delegates = mutableListOf<TouchDelegate>()

    fun addDelegate(touchDelegate: TouchDelegate) {
        _delegates += touchDelegate
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }
}

operator fun CompositeTouchDelegate.plusAssign(touchDelegate: TouchDelegate) {
    addDelegate(touchDelegate)
}

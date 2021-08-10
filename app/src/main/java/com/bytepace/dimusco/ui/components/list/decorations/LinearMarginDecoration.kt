package com.bytepace.dimusco.ui.components.list.decorations

import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class LinearMarginDecoration(
    private val orientation: Int,
    private val horizontalMargins: Int,
    private val verticalMargins: Int = horizontalMargins,
    private val outlined: Boolean = true
) : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val lastIndex = parent.adapter?.itemCount?.dec() ?: return
        when (orientation) {
            LinearLayout.HORIZONTAL -> showHorizontalMargins(position, outRect, lastIndex)
            LinearLayout.VERTICAL -> showVerticalMargins(position, outRect, lastIndex)
        }
    }

    private fun showHorizontalMargins(position: Int, outRect: Rect, lastIndex: Int) {
        outRect.left = horizontalMargins / 2
        outRect.right = horizontalMargins / 2
        outRect.top = verticalMargins
        outRect.bottom = verticalMargins
        if (position == 0) {
            outRect.left = when (outlined) {
                true -> horizontalMargins
                else -> 0
            }
        }
        if (position == lastIndex) {
            outRect.right = when (outlined) {
                true -> horizontalMargins
                else -> 0
            }
        }
    }

    private fun showVerticalMargins(position: Int, outRect: Rect, lastIndex: Int) {
        outRect.left = horizontalMargins
        outRect.right = horizontalMargins
        outRect.top = verticalMargins / 2
        outRect.bottom = verticalMargins / 2
        if (position == 0) {
            outRect.top = when (outlined) {
                true -> verticalMargins
                else -> 0
            }
        }
        if (position == lastIndex) {
            outRect.bottom = when (outlined) {
                true -> verticalMargins
                else -> 0
            }
        }
    }
}
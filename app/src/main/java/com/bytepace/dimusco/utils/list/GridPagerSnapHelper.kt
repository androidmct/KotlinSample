package com.bytepace.dimusco.utils.list

import android.view.View
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class GridPagerSnapHelper(var spanCount: Int) : PagerSnapHelper() {

    private lateinit var mVerticalHelper: OrientationHelper
    private lateinit var mHorizontalHelper: OrientationHelper

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val out = intArrayOf(0, 0)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToSnap(targetView, getHorizontalHelper(layoutManager))
        }
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToSnap(targetView, getVerticalHelper(layoutManager))
        }
        return out
    }

    private fun distanceToSnap(targetView: View, helper: OrientationHelper): Int {
        return helper.getDecoratedStart(targetView) - helper.startAfterPadding
    }

    private fun findStartView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null
        }

        var closestChild: View? = null
        var startest = Integer.MAX_VALUE

        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val childStart = helper.getDecoratedStart(child)

            /** if child is more to start than previous closest, set it as closest   */
            if (childStart < startest) {
                startest = childStart
                closestChild = child
            }
        }
        return closestChild
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (!::mVerticalHelper.isInitialized || mVerticalHelper.layoutManager !== layoutManager) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return mVerticalHelper
    }

    private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (!::mHorizontalHelper.isInitialized || mHorizontalHelper.layoutManager !== layoutManager) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return mHorizontalHelper
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        val itemCount = layoutManager.itemCount
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION
        }

        var mStartMostChildView: View? = null
        if (layoutManager.canScrollVertically()) {
            mStartMostChildView = findStartView(layoutManager, getVerticalHelper(layoutManager))
        } else if (layoutManager.canScrollHorizontally()) {
            mStartMostChildView = findStartView(layoutManager, getHorizontalHelper(layoutManager))
        }

        if (mStartMostChildView == null) {
            return RecyclerView.NO_POSITION
        }
        val centerPosition = layoutManager.getPosition(mStartMostChildView)
        if (centerPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }

        val forwardDirection: Boolean
        if (layoutManager.canScrollHorizontally()) {
            forwardDirection = velocityX > 0
        } else {
            forwardDirection = velocityY > 0
        }
        var reverseLayout = false
        if (layoutManager is RecyclerView.SmoothScroller.ScrollVectorProvider) {
            val vectorProvider = layoutManager as RecyclerView.SmoothScroller.ScrollVectorProvider
            val vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1)
            if (vectorForEnd != null) {
                reverseLayout = vectorForEnd.x < 0 || vectorForEnd.y < 0
            }
        }
        return if (reverseLayout)
            if (forwardDirection) centerPosition - spanCount else centerPosition
        else
            if (forwardDirection) centerPosition + spanCount else centerPosition
    }
}
package com.exd.myapplication.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import kotlin.math.absoluteValue


class OverScrollBehavior(
    context: Context,
    attributeSet: AttributeSet
) : CoordinatorLayout.Behavior<View>() {

    private var overScrollY = 0

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        overScrollY = 0
        Log.e(TAG, "onStartNestedScroll: " )
        return true
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        if (dyUnconsumed == 0 || dyUnconsumed.absoluteValue > 150) {
            Log.e(TAG, "onNestedScroll: ignoring $dyUnconsumed" )
            return
        } else if (overScrollY > OVERSCROLL_MAX) {
            Log.e(TAG, "onNestedScroll: cap" )
            return
        }

        Log.e(TAG, "onNestedScroll: $dyUnconsumed $overScrollY" )
        overScrollY -= dyUnconsumed
        val group = target as ViewGroup
        val count = group.childCount
        for (i in 0 until count) {
            val view = group.getChildAt(i)
            view.translationY = overScrollY.toFloat()
        }
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        type: Int
    ) {
        val group = target as ViewGroup
        val count = group.childCount
        for (i in 0 until count) {
            val view = group.getChildAt(i)
            ViewCompat.animate(view).translationY(0f).start()
        }
    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.e(TAG, "onNestedPreFling: velocityY $velocityY")
        return true
    }
}

val TAG = "OVERSCROLL"
val OVERSCROLL_MAX = 200
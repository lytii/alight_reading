package com.exd.myapplication.view

import android.util.Log
import androidx.recyclerview.widget.RecyclerView

class OverScrollEffectListener(val model: ChapterViewModel) : RecyclerView.OnScrollListener() {
    val TAG = "OverScrollListener"
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val canScrollDown = recyclerView.canScrollVertically(1)
        val canScrollUp = recyclerView.canScrollVertically(-1)
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            model.onBottomReached(!canScrollDown)
            model.onTopReached(!canScrollUp)
        }
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            if (!canScrollDown) {
                model.onOverScroll()
            }
            if (!canScrollUp) {
                model.onOverScrollUp()
            }
        }
        val state = when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> "SCROLL_STATE_IDLE"
            RecyclerView.SCROLL_STATE_DRAGGING -> "SCROLL_STATE_DRAGGING"
            RecyclerView.SCROLL_STATE_SETTLING -> "SCROLL_STATE_SETTLING"
            else -> "SCROLL_UNKNOWN"
        }
        Log.v(TAG, "onScrollStateChanged: $state $canScrollDown")
    }
}
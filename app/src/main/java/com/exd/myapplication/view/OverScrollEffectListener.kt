package com.exd.myapplication.view

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*

class OverScrollEffectListener(val model: ChapterViewModel) : RecyclerView.OnScrollListener() {
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val canScrollDown = recyclerView.canScrollVertically(1)
        val canScrollUp = recyclerView.canScrollVertically(-1)
        if (newState == SCROLL_STATE_IDLE) {
            model.onBottomReached(!canScrollDown)
            model.onTopReached(!canScrollUp)
        }
        if (newState == SCROLL_STATE_DRAGGING) {
            if (!canScrollDown) {
                model.onOverScroll()
            }
            if (!canScrollUp) {
                model.onOverScrollUp()
            }
        }
        val state = when (newState) {
            SCROLL_STATE_IDLE -> "SCROLL_STATE_IDLE"
            SCROLL_STATE_DRAGGING -> "SCROLL_STATE_DRAGGING"
            SCROLL_STATE_SETTLING -> "SCROLL_STATE_SETTLING"
            else -> "SCROLL_UNKNOWN"
        }
//        Log.v(TAG, "onScrollStateChanged: $state $canScrollDown")
    }
}


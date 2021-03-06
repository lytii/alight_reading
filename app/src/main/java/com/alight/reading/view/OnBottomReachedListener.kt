package com.alight.reading.view

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.alight.reading.view.chapter.TAG

class OnBottomReachedListener(private val onBottomReached: () -> Unit) : OnScrollListener() {
    var startedLeaving = false
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val canScrollDown = recyclerView.canScrollVertically(1)
        val states = listOf(
            SCROLL_STATE_IDLE,
            SCROLL_STATE_DRAGGING
        )
        val state = when (newState) {
            SCROLL_STATE_IDLE -> "SCROLL_STATE_IDLE"
            SCROLL_STATE_DRAGGING -> "SCROLL_STATE_DRAGGING"
            SCROLL_STATE_SETTLING -> "SCROLL_STATE_SETTLING"
            else -> "SCROLL_UNKNOWN"
        }

        if (newState == SCROLL_STATE_DRAGGING && !canScrollDown) {
            // started leaving bottom
            Log.v(TAG, "onScrollStateChanged: started leaving bottom $state")
        }
        if (newState == SCROLL_STATE_IDLE && canScrollDown) {
            // ending leaving bottom
            Log.v(TAG, "onScrollStateChanged: ending leaving bottom $state")
        }

        if (newState == SCROLL_STATE_SETTLING && !canScrollDown) {
            Log.v(TAG, "onScrollStateChanged: setting can't scroll down")
        }

        if (newState == SCROLL_STATE_SETTLING && canScrollDown) {
            Log.v(TAG, "onScrollStateChanged: setting can scroll down")
        }

        if (newState == SCROLL_STATE_DRAGGING && canScrollDown) {
            // started reaching bottom
            Log.v(TAG, "onScrollStateChanged: started reaching bottom $state")
            onBottomReached.invoke()

        }
        if (newState == SCROLL_STATE_IDLE && !canScrollDown) {
            // ending reaching bottom
            Log.v(TAG, "onScrollStateChanged: ending reaching bottom $state")
        }
    }
}
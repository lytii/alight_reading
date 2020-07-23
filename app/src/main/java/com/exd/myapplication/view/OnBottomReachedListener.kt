package com.exd.myapplication.view

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*

class OnBottomReachedListener(
//    onBottomReached: (Boolean) -> Unit
) : OnScrollListener() {
    val TAG = "OverScrollListener"
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
            Log.e(TAG, "onScrollStateChanged: started leaving bottom $state")
        }
        if (newState == SCROLL_STATE_IDLE && canScrollDown) {
            // ending leaving bottom
            Log.e(TAG, "onScrollStateChanged: ending leaving bottom $state")
        }

        if(newState == SCROLL_STATE_SETTLING && !canScrollDown) {
            Log.e(TAG, "onScrollStateChanged: setting can't scroll down")
        }

        if(newState == SCROLL_STATE_SETTLING && canScrollDown) {
            Log.e(TAG, "onScrollStateChanged: setting can scroll down")
        }

        if (newState == SCROLL_STATE_DRAGGING && canScrollDown) {
            // started reaching bottom
            Log.e(TAG, "onScrollStateChanged: started reaching bottom $state")

        }
        if (newState == SCROLL_STATE_IDLE && !canScrollDown) {
            // ending reaching bottom
            Log.e(TAG, "onScrollStateChanged: ending reaching bottom $state")
        }
    }
}
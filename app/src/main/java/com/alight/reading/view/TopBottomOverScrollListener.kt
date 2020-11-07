package com.alight.reading.view

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alight.reading.view.chapter.ChapterViewModel

class TopBottomOverScrollListener(
    private val model: ChapterViewModel,
    context: Context
) : LinearLayoutManager(context) {
    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val msg = when {
            dy > 300 -> model.onStrongScrollDown()
            dy < -300 -> model.onStrongScrollUp()
            dy < -200 -> "medium"
            dy < -100 -> "weak"
            else -> "nothing"
        }
//                Log.e(TAG, "scrollVerticallyBy: $msg up")
        return super.scrollVerticallyBy(dy, recycler, state)
    }
}

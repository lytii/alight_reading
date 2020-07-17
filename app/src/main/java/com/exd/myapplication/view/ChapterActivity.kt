package com.exd.myapplication.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.exd.myapplication.R
import com.exd.myapplication.models.Chapter

class ChapterActivity : AppCompatActivity() {
    val TAG = "CHAPTER ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)
        buildComponent()
        setViewModels()
    }

    private fun buildComponent() {
        DaggerActivityComponent.builder()
            .bindContext(this.applicationContext)
            .build()
            .let { ActivityComponent.instance = it }
    }

    private fun setViewModels() {
        val model: ChapterViewModel by viewModels()
        val paragraphList = findViewById<RecyclerView>(R.id.paragraph_list)

        paragraphList.addOnScrollListener(OverScrollEffectListener(model))

        val layoutManager = TopBottomOverScrollListener(model, applicationContext)
        paragraphList.layoutManager = layoutManager

        val adapter = ChapterAdapter()
        paragraphList.adapter = adapter
        paragraphList.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            Log.e("scroll", "$oldScrollY to $scrollY")

            if (oldScrollY <= scrollY && oldScrollY != 0) {
//                title.visibility = View.GONE
//                Log.e("scroll", "down $oldScrollY to $scrollY")
            } else {
//                Log.e("scroll", "up")
//                title.visibility = View.VISIBLE
            }
        }
        Log.v("chapter activity", "listening to chapter")
        val chapterObserver = Observer<Chapter> { chapter ->
            Log.v(TAG, "setViewModels: ${chapter.chapterTitle}")
//            title.text = Html.fromHtml(chapter.chapterTitle, FROM_HTML_MODE_COMPACT)
            paragraphList.scrollToPosition(0)
            adapter.setChapter(chapter)
        }
        model.chapterData.observe(this, chapterObserver)
        model.getBook()
    }
}

class TopBottomOverScrollListener(
    private val model: ChapterViewModel,
    context: Context
) : LinearLayoutManager(context) {
    override fun scrollVerticallyBy(dy: Int, recycler: Recycler?, state: State?): Int {
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

class OverScrollEffectListener(val model: ChapterViewModel) : OnScrollListener() {
    val TAG = "OverScrollListener"
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
        Log.v(TAG, "onScrollStateChanged: $state $canScrollDown")
    }
}

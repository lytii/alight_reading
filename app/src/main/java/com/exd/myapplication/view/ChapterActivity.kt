package com.exd.myapplication.view

import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.util.Log
import android.widget.TextView
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
        val component = DaggerActivityComponent
            .builder()
            .bindContext(this.applicationContext)
            .build()
        ActivityComponent.instance = component
        setViewModels()
    }

    private fun setViewModels() {
        val model: ChapterViewModel by viewModels()
        val title = findViewById<TextView>(R.id.title)

        val paragraphList = findViewById<RecyclerView>(R.id.paragraph_list)
        paragraphList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        })


        val adapter = ChapterAdapter()
        paragraphList.adapter = adapter
//        paragraphList.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            if(oldScrollY > scrollY) {
//                Log.e("scroll", "up")
//                title.visibility = View.VISIBLE
//            } else {
//                title.visibility = View.GONE
//                Log.e("scroll", "down")
//            }
//        }
        Log.v("chapter activity", "listening to chapter")
        val chapterObserver = Observer<Chapter> { chapter ->
            Log.v(TAG, "setViewModels: ${chapter.chapterTitle}")
            title.text = Html.fromHtml(chapter.chapterTitle, FROM_HTML_MODE_COMPACT)
            adapter.setChapter(chapter)
        }
        model.chapterData.observe(this, chapterObserver)
        model.setChapter()
    }
}

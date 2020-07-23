package com.exd.myapplication.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exd.myapplication.R
import com.exd.myapplication.dagger.ActivityComponent
import com.exd.myapplication.dagger.DaggerActivityComponent
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

//        val adapter = ChapterAdapter()
//        paragraphList.adapter = adapter
//        paragraphList.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
////            Log.e("scroll", "$oldScrollY to $scrollY")
//
//            if (oldScrollY <= scrollY && oldScrollY != 0) {
////                title.visibility = View.GONE
////                Log.e("scroll", "down $oldScrollY to $scrollY")
//            } else {
////                Log.e("scroll", "up")
////                title.visibility = View.VISIBLE
//            }
//        }
//        Log.v("chapter activity", "listening to chapter")
//        val chapterObserver = Observer<Chapter> { chapter ->
//            Log.v(TAG, "setViewModels: ${chapter.chapterId} ${chapter.chapterTitle}")
////            title.text = Html.fromHtml(chapter.chapterTitle, FROM_HTML_MODE_COMPACT)
//            adapter.setChapter(chapter)
//            (paragraphList.layoutManager as LinearLayoutManager).scrollToPosition(0)
//            paragraphList.smoothScrollToPosition(0)
//        }
//        model.chapterDataToBeObserved.observe(this, chapterObserver)
//        model.loadContent()
    }
}

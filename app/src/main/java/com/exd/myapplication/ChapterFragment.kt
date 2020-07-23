package com.exd.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.view.*
import java.lang.IllegalStateException

class ChapterFragment : Fragment(), ChapterNavigationListener {

    companion object {
        fun newInstance() = ChapterFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_chapter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setViewModels()
    }


    private val viewModel: ChapterViewModel by viewModels()

    private fun setViewModels() {
        val paragraphList = view?.findViewById<RecyclerView>(R.id.paragraph_list)
            ?: throw IllegalStateException("null view for some reason")

        val adapter = ChapterAdapter(this)
        paragraphList.adapter = adapter
//        paragraphList.addOnScrollListener(OnBottomReachedListener())

        Log.v(TAG, "listening to chapter")
        val chapterObserver = Observer<Chapter> { chapter ->
            Log.v(TAG, "setViewModels: ${chapter.chapterId} ${chapter.chapterTitle}")
            adapter.setChapter(chapter)
            val position = if (prev) chapter.paragraphs.size + 1 else 0
            paragraphList.scrollToPosition(position)
        }
        viewModel.chapterDataToBeObserved.observe(viewLifecycleOwner, chapterObserver)
        viewModel.loadContent(true)
    }

    var prev = false

    override fun onNext() {
        Log.e(TAG, "onNext: ")
//        prev = false
        viewModel.nextChapter()
    }

    override fun onPrev() {
        Log.e(TAG, "onPrev: ")
//        prev = true
        viewModel.previousChapter()
    }
}

interface ChapterNavigationListener {
    fun onNext()
    fun onPrev()
}

const val TAG = "blank fragment"
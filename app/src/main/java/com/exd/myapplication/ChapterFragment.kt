package com.exd.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.ui.chapterlist.toScrollState
import com.exd.myapplication.view.ChapterAdapter
import com.exd.myapplication.view.ChapterViewModel
import kotlinx.android.synthetic.main.activity_chapter.view.*
import me.everything.android.ui.overscroll.IOverScrollState.STATE_BOUNCE_BACK
import me.everything.android.ui.overscroll.IOverScrollState.STATE_IDLE
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

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
        activity?.window?.statusBarColor = resources.getColor(R.color.black, null)
        super.onActivityCreated(savedInstanceState)
    }

    var addPrev = false
    var addNext = false

    val bottomReachListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (!recyclerView.canScrollVertically(1)) {
                viewModel.onBottomReached(true)
            }
        }
    }

    private fun observeWith(paragraphList: RecyclerView, adapter: ChapterAdapter) =
        Observer<ChapterViewModel.ChapterState> { (chapter, direction, scroll) ->
            paragraphList.clearOnScrollListeners()
            paragraphList.addOnScrollListener(bottomReachListener)
            Log.v(TAG, "setViewModels: ${chapter.overallString()}")
//            val position = if (prev) chapter.paragraphs.size + 1 else 0

            when (direction) {
                ChapterViewModel.ChapterDirection.NEXT -> {
                    adapter.setNextChapter(chapter)
                    Log.e(TAG, "scroll next ${adapter.nextChapterIndex}")
                    if (scroll) {
                        paragraphList.scrollToPosition(adapter.nextChapterIndex)
                        paragraphList.smoothScrollBy(0, 1000)
                    }
                }
                ChapterViewModel.ChapterDirection.PREV -> {
                    Log.e(TAG, "scroll prev?")
                    adapter.setPrevChapter(chapter)
                    paragraphList.scrollToPosition(adapter.prevChapterIndex)
                }
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setViewModels()
        val paragraphList = view.findViewById<RecyclerView>(R.id.paragraph_list)
            ?: throw IllegalStateException("null view for some reason")

        val adapter = ChapterAdapter(this)
        paragraphList.adapter = adapter

        Log.v(TAG, "listening to chapter")
        val chapterObserver = observeWith(paragraphList, adapter)
        viewModel.chapterDataToBeObserved.observe(viewLifecycleOwner, chapterObserver)
        loadChapterFromArguments()

//        val decor = OverScrollDecoratorHelper.setUpOverScroll(
//            view.paragraph_list,
//            OverScrollDecoratorHelper.ORIENTATION_VERTICAL
//        )
//        decor.setOverScrollStateListener { decor, oldState, newState ->
//            Log.v(TAG, "onViewCreated: ${oldState.toScrollState()} ${newState.toScrollState()}")
//            if (oldState == STATE_BOUNCE_BACK && newState == STATE_IDLE) {
//                when {
//                    addPrev -> onPrev()
//                    addNext -> onNext()
//                }
//            }
//        }
//        decor.setOverScrollUpdateListener { decor, state, offset ->
//            when {
//                offset > 300 -> addPrev = true
//                offset < -300 -> addNext = true
//            }
//        }
    }

    private fun loadChapterFromArguments() {
        Log.w(TAG, "loadChapterFromArguments: ")
        arguments?.getString("chapterUrl")?.let { viewModel.loadUrl(it) }
    }

    infix fun Int.stateTo(other: Int): String {
        val s = this.toScrollState()
        val o = other.toScrollState()
        return "$s to $o"
    }

    private val viewModel: ChapterViewModel by viewModels()

    private fun setViewModels() {
    }

    var prev = false

    override fun onNext() {
        addNext = true
        Log.e(TAG, "onNext: ")
//        prev = false
        viewModel.nextChapter()
    }

    override fun onPrev() {
        addPrev = true
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
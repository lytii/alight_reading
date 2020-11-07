package com.alight.reading

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alight.reading.ui.chapterlist.toScrollState
import com.alight.reading.view.chapter.ChapterAdapter
import com.alight.reading.view.chapter.ChapterDirection
import com.alight.reading.view.chapter.ChapterState
import com.alight.reading.view.chapter.ChapterViewModel
import kotlinx.android.synthetic.main.activity_chapter.*

class ChapterFragment : Fragment(), ChapterNavigationListener {

    companion object {
        const val SCROLL_POS = "scroll_pos"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_chapter, container, false)
    }

    override fun onPause() {
        super.onPause()
        Log.w(TAG, "onPause: ")
        val layout = (paragraph_list.layoutManager as LinearLayoutManager)
        arguments?.putInt(SCROLL_POS, layout.findFirstVisibleItemPosition())
    }
    // opening
//    override fun onResume() {
//        super.onResume()
//        val pos = arguments?.getInt(SCROLL_POS, 0)
//        pos?.let {
//            Log.w(TAG, "onResume: $pos")
//            paragraph_list.scrollToPosition(pos)
//        }
//    }

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
        Observer<ChapterState> { (chapter, direction, scroll, pos) ->
            paragraphList.clearOnScrollListeners()
            paragraphList.addOnScrollListener(bottomReachListener)
            Log.v(TAG, "observeWith: $direction $scroll $pos")
            Log.v(TAG, "setViewModels: ${chapter.overallString()}")
//            val position = if (prev) chapter.paragraphs.size + 1 else 0

            when (direction) {
                ChapterDirection.NEXT -> {
                    adapter.setNextChapter(chapter)
                    if (scroll) {
                        Log.e(TAG, "scroll next ${adapter.nextChapterIndex}")
                        if (pos != 0) {
                            paragraphList.scrollToPosition(pos)
                        } else {
                            paragraphList.scrollToPosition(adapter.nextChapterIndex)
                        }
                        paragraphList.smoothScrollBy(0, 1000)
                    }
                }
                ChapterDirection.PREV -> {
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
        val pos = arguments?.getInt(SCROLL_POS, 0) ?: 0
//        pos?.let {
//            Log.w(TAG, "onResume: $pos")
//            paragraph_list.scrollToPosition(pos)
//        }

        arguments?.getString("chapterUrl")?.let {
            Log.w(TAG, "loadChapterFromArguments: $it")
            viewModel.loadUrl(it, getScrollPos())
        }
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
        resetScrollPos()
        viewModel.nextChapter()
    }

    override fun onPrev() {
        addPrev = true
        Log.e(TAG, "onPrev: ")
        resetScrollPos()
        viewModel.previousChapter()
    }

    private fun resetScrollPos() {
        arguments?.putInt(SCROLL_POS, 0)
    }

    private fun getScrollPos(): Int {
        return arguments?.getInt(SCROLL_POS, 0) ?: 0
    }
}

interface ChapterNavigationListener {
    fun onNext()
    fun onPrev()
}

const val TAG = "blank fragment"
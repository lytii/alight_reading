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
import com.exd.myapplication.view.OnBottomReachedListener
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setViewModels()
        val paragraphList = view?.findViewById<RecyclerView>(R.id.paragraph_list)
            ?: throw IllegalStateException("null view for some reason")

        val adapter = ChapterAdapter(this)
        paragraphList.adapter = adapter
        paragraphList.addOnScrollListener(OnBottomReachedListener())

        Log.v(TAG, "listening to chapter")
        val chapterObserver = Observer<Chapter> { chapter ->
            Log.v(TAG, "setViewModels: ${chapter.chapterId} ${chapter.chapterTitle}")
            adapter.setChapter(chapter)
//            val position = if (prev) chapter.paragraphs.size + 1 else 0
            
            when {
                addNext -> {
                    addNext = false
                    Log.e(TAG, "scroll next ${adapter.nextChapterIndex}")
                    paragraphList.smoothScrollBy(0, 600)

                }
                addPrev -> {
                    paragraphList.scrollToPosition(adapter.prevChapterIndex + 2)
                    addPrev = false
                }
            }

        }
        viewModel.chapterDataToBeObserved.observe(viewLifecycleOwner, chapterObserver)
//        viewModel.loadContent(true)
        arguments?.getString("chapterUrl")?.let {
            viewModel.loadUrl(it)
        }

        val decor = OverScrollDecoratorHelper.setUpOverScroll(
            view.paragraph_list,
            OverScrollDecoratorHelper.ORIENTATION_VERTICAL
        )
        decor.setOverScrollStateListener { decor, oldState, newState ->
            if (oldState == STATE_BOUNCE_BACK && newState == STATE_IDLE) {
                when {
                    addPrev -> onPrev()
                    addNext -> onNext()
                }
            }
        }
        decor.setOverScrollUpdateListener { decor, state, offset ->
            when {
                offset > 300 -> addPrev = true
                offset < -300 -> addNext = true
            }
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
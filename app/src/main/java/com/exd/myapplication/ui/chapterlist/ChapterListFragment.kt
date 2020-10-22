package com.exd.myapplication.ui.chapterlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.exd.myapplication.R
import com.exd.myapplication.TAG
import com.exd.myapplication.dagger.ActivityComponent
import com.exd.myapplication.models.Chapter
import com.google.android.material.appbar.AppBarLayout
import dagger.Component
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_chapter_list.view.*
import kotlinx.android.synthetic.main.item_chapter.view.*
import me.everything.android.ui.overscroll.IOverScrollState.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.util.concurrent.TimeUnit


interface ChapterListListener {
    fun goToChapter(chapterUrl: String)
    fun markPreviousAsRead(index: Int)
}

class ChapterListFragment : Fragment(), ChapterListListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_chapter_list, container, false)

    private var adapter = ChapterListAdapter(this)
    private lateinit var scrollingSummaryDisposable: Disposable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.window?.statusBarColor = resources.getColor(android.R.color.transparent, null)
        super.onViewCreated(view, savedInstanceState)
        view.chapter_list.adapter = adapter
//        val decor = OverScrollDecoratorHelper.setUpOverScroll(
//            view.chapter_list,
//            OverScrollDecoratorHelper.ORIENTATION_VERTICAL
//        )
//        decor.setOverScrollStateListener { decor, oldState, newState ->
//            Log.e(TAG, "scroll listener ${oldState.toScrollState()} to ${newState.toScrollState()}")
//        }
//        decor.setOverScrollUpdateListener { decor, state, offset ->
//            Log.e(TAG, "scroll update ${state.toScrollState()}: $offset")
//        }
        setUpToolbar(view)
    }

    private fun setUpToolbar(view: View) {
        // toolbar
        (activity as AppCompatActivity).run { setSupportActionBar(view.toolbar) }
        // toolbar cover image
        val coverDrawable = ResourcesCompat.getDrawable(resources, R.drawable.vm_novel_image, null)
        view.cover_image.setImageDrawable(coverDrawable)
        // toolbar title (mostly for collapsed title)
        val title = getString(R.string.death_march_title)
        view.collapsing_toolbar.title = title
        view.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, scrollPos ->
            if (-scrollPos == appBarLayout?.totalScrollRange) {
                scrollingSummaryDisposable.safeDispose()
                Log.e(TAG, "onOffsetChanged: collapsed")
            } else {
                scrollingSummaryDisposable.safeDispose()
                scrollingSummaryDisposable = view.book_summary_scroll.slowScroll()
                Log.e(TAG, "onOffsetChanged: expanded")
            }
        })

        scrollingSummaryDisposable = view.book_summary_scroll.slowScroll()
        // disables user touch scrolling on scrollview
        view.book_summary_scroll.setOnTouchListener { _, _ -> true }
    }

    override fun goToChapter(chapterUrl: String) {
        Thread.sleep(200) // selectable background animation to play before navigating
        val bundle = bundleOf("chapterUrl" to chapterUrl)
        findNavController().navigate(R.id.chapterFragment, bundle)
    }

    override fun markPreviousAsRead(index: Int) {
        viewModel.markPreviousAsRead(index)
    }

    private val wait = 80
    private var scroll = -wait

    /**
     * 1. Pause at top,
     * 2. slowly scroll down,
     * 3. pause at bottom,
     * 4. reset to top, repeat step 1.
     * @param interval : refresh rate to scroll
     * @param ySpeed : scrolling speed, this low and low [interval] will look smoother
     */
    fun ScrollView.slowScroll(interval: Long = 50, ySpeed: Int = 1): Disposable {
        return Observable.interval(interval, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when {
                    // when negative scroll will increment while doing nothing
                    scroll < 0 -> scroll++
                    // scroll down continuously
                    canScrollVertically(1) -> smoothScrollBy(0, ySpeed)
                    // wait when we can't scroll anymore
                    scroll < wait -> scroll++
                    // reset once we reach bottom and wait limit
                    else -> {
                        smoothScrollTo(0, 0)
                        // wait when we are at top
                        scroll = -wait
                    }
                }
            }, { Log.e(TAG, "slowScroll", it) }
            )
    }


    private fun Disposable.safeDispose() {
        if (!this.isDisposed) this.dispose()
    }

    private val viewModel: ChapterListViewModel by viewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setViewModels()
    }

    private fun setViewModels() {
        viewModel.chapterData.observe(viewLifecycleOwner) { chapterList ->
            adapter.setChapterList(chapterList)
            // todo change to isRead
            val index = chapterList.last { it.isCached }.index
            val scroll = if (index < 3) {
                0
            } else {
                index - 3
            }

            view?.chapter_list
                ?.scrollToPosition(scroll)
        }
        viewModel.getChapterList()
    }
}

@Component(dependencies = [ActivityComponent::class])
interface Injector {
    fun inject(viewModel: ChapterListViewModel)

    fun injector(): Injector
}

class ChapterListAdapter(val chapterListListener: ChapterListListener) :
    RecyclerView.Adapter<ChapterListHolder>() {
    val TAG = "ChapterListAdapter"

    private var chapterList: List<Chapter> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterListHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chapter, parent, false)
            .let { ChapterListHolder(it, chapterListListener) }
    }

    fun setChapterList(list: List<Chapter>) {
        this.chapterList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = chapterList.size

    override fun onBindViewHolder(holder: ChapterListHolder, position: Int) {
        if (position >= chapterList.size) {
            Log.e(TAG, "onBindViewHolder: out of bounds ($position)")
        }
        holder.bind(chapterList[position])
    }
}

class ChapterListHolder(view: View, val chapterListListener: ChapterListListener) :
    RecyclerView.ViewHolder(view) {

    fun bind(chapter: Chapter) {
        itemView.setOnClickListener { chapterListListener.goToChapter(chapter.chapterUrl) }
        itemView.title.text = chapter.chapterTitle
        val drawable = if (chapter.isCached) {
            ResourcesCompat.getDrawable(itemView.resources, R.drawable.ic_list_24, null)
        } else {
            null
        }
        itemView.icon.setImageDrawable(drawable)
        itemView.setOnLongClickListener {
            Log.e(TAG, "bind: marking previous as read")
            Toast.makeText(
                itemView.context,
                "marking previous as read, need to reload",
                Toast.LENGTH_SHORT
            ).show()
            chapterListListener.markPreviousAsRead(chapter.index)
                .let { true }
        }
    }
}

fun Int.toScrollState(): String {
    return when (this) {
        STATE_IDLE -> "STATE_IDLE"
        STATE_DRAG_START_SIDE -> "STATE_DRAG_START_SIDE"
        STATE_DRAG_END_SIDE -> "STATE_DRAG_END_SIDE"
        STATE_BOUNCE_BACK -> "STATE_BOUNCE_BACK"
        else -> "IDK STATE($this)"
    }
}
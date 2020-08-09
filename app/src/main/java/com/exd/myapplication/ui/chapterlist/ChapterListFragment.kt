package com.exd.myapplication.ui.chapterlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.exd.myapplication.R
import com.exd.myapplication.dagger.ActivityComponent
import com.exd.myapplication.models.Chapter
import dagger.Component
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_chapter_list.view.*
import kotlinx.android.synthetic.main.item_chapter.view.*
import java.util.concurrent.TimeUnit

class ChapterListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_chapter_list, container, false)

    private var adapter = ChapterListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.chapter_list.adapter = adapter
        val wait = 80/2
        var scroll = -wait
        // disables user touch scrolling on scrollview
        view.book_summary_scroll.setOnTouchListener { v, event -> true }
        Observable.interval(50, TimeUnit.MILLISECONDS)
            .subscribe {
                with(view.book_summary_scroll) {
                    when {
                        scroll < 0 -> scroll++
                        canScrollVertically(1) -> smoothScrollBy(0, 1)
                        scroll < wait -> scroll++
                        else -> {
                            smoothScrollTo(0, 0)
                            scroll = -wait
                        }
                    }
                }
            }

        (activity as AppCompatActivity).run { setSupportActionBar(view.toolbar) }
        view.cover_image.setImageDrawable(
            resources.getDrawable(R.drawable.vm_novel_image, null)
        )
        val title = getString(R.string.vending_machine_title)
        view.collapsing_toolbar.title = title
    }

    private val viewModel: ChapterListViewModel by viewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setViewModels()
    }

    private fun setViewModels() {
        viewModel.chapterData.observe(viewLifecycleOwner) { chapterList ->
            adapter.setChapterList(chapterList)
        }
        viewModel.getChapterList()
    }
}

@Component(dependencies = [ActivityComponent::class])
interface Injector {
    fun inject(viewModel: ChapterListViewModel)

    fun injector(): Injector
}

class ChapterListAdapter : RecyclerView.Adapter<ChapterListHolder>() {
    val TAG = "ChapterListAdapter"

    private var chapterList: List<Chapter> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterListHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chapter, parent, false)
            .let { ChapterListHolder(it) }
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
        holder.bind(chapterList[position].chapterTitle)
    }
}

class ChapterListHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(title: String) {
        itemView.title.text = title
    }
}

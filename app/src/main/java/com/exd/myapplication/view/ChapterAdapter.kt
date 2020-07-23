package com.exd.myapplication.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.exd.myapplication.ChapterNavigationListener
import com.exd.myapplication.R
import com.exd.myapplication.models.Chapter

class ChapterAdapter(
    private val listener: ChapterNavigationListener
) : RecyclerView.Adapter<ChapterHolder>() {
    private var chapter: Chapter = Chapter.emptyChapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterHolder {
        return when (viewType) {
            NAVIGATION -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_navigation, parent, false)
                .let { NavigationHolder(it, listener) }
            else -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_paragraph, parent, false)
                .let { ParagraphHolder(it) }
        }
    }

    fun setChapter(chapter: Chapter) {
        this.chapter = chapter
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position !in firstAndLast().value) {
            PARAGRAPH
        } else {
            NAVIGATION
        }
    }

    override fun getItemCount(): Int = chapter.paragraphs.size + 2 // add first and last

    private fun firstAndLast() = lazy {
        listOf(0, chapter.paragraphs.size + 1)
    }

    override fun onBindViewHolder(holder: ChapterHolder, position: Int) {
        if (position !in firstAndLast().value && chapter.paragraphs.isNotEmpty()) {
            (holder as ParagraphHolder).bind(chapter.paragraphs[position - 1].text)
        } else {
            (holder as NavigationHolder).bind()
        }
    }
}

const val PARAGRAPH = 1
const val NAVIGATION = 2


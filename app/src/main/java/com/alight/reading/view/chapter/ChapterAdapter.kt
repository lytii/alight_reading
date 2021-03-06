package com.alight.reading.view.chapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alight.reading.ChapterNavigationListener
import com.alight.reading.R
import com.alight.reading.models.Chapter
import com.alight.reading.view.ChapterHolder
import com.alight.reading.view.NavigationHolder
import com.alight.reading.view.ParagraphHolder

val Any.TAG get() = this.javaClass.name

class ChapterAdapter(
    private val listener: ChapterNavigationListener
) : RecyclerView.Adapter<ChapterHolder>() {
    //    private var chapter: Chapter = Chapter.emptyChapter
    private var chapters: MutableList<Chapter> = mutableListOf()
    val prevChapterIndex: Int
        get() = navIndices[chapterListIndex]
    val nextChapterIndex: Int
        get() = navIndices[navIndices.lastIndex - 1]


    var chapterListIndex = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterHolder {
        return when (viewType) {
            NAVIGATION -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_navigation, parent, false)
                .let {
                    NavigationHolder(
                        it,
                        listener
                    )
                }
            else -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_paragraph, parent, false)
                .let { ParagraphHolder(it) }
        }
    }

    fun setPrevChapter(chapter: Chapter) {
        chapters.add(0, chapter)
        chapterListIndex--
        setupNavIndices()
        notifyDataSetChanged()
    }

    fun setNextChapter(chapter: Chapter) {
        chapters.add(chapter)
        chapterListIndex++.also { Log.d(TAG, "chapterList Index: $chapterListIndex") }
        setupNavIndices()
        notifyDataSetChanged()
    }

    private val navIndices = mutableListOf(0)

    /**
     * Gets last index of each chapter to add onto indices indicating nav positions
     */
    private fun setupNavIndices() {
        navIndices.clear()
        navIndices.add(0)
        chapters.forEach {
            val endChapterNavIndex = navIndices.last() + it.paragraphs.size + 1
            navIndices.add(endChapterNavIndex)
        }
    }

    override fun getItemViewType(position: Int): Int {
        navIndices.forEach { navIndex ->
            if (navIndex == position) {
                return NAVIGATION
            }
        }
        return PARAGRAPH
    }
//    override fun getItemCount(): Int = chapter.paragraphs.size + 2 // add first and last

    override fun getItemCount(): Int {
        return chapters.sumBy { it.paragraphs.size + 1 } + 1
    }

//    private fun firstAndLast() = lazy {
//        listOf(0, chapter.paragraphs.size + 1)
//    }

    override fun onBindViewHolder(holder: ChapterHolder, position: Int) {
        // 0 = nav
        // 1..chapter0Size = chapter0
        //      chapter[pos-1]
        // chapter0Size+1 = nav
        // chapter0Size+2..chapter0Size+2+chapter1Size+1 = chapter1
        //                 ^ should be equal to itemCount
        //      chapter[pos-chapter0Size-index]
        //

        var prevIndex = 0
        navIndices.forEachIndexed { index, navIndex ->
            if (navIndex == position) {
                return (holder as NavigationHolder).bind()
            }
            if (navIndex > position) {
                val paragraph = chapters[index - 1].paragraphs[position - prevIndex - 1]
                return (holder as ParagraphHolder).bind(paragraph.text)
            }
            prevIndex = navIndex
        }
        // 0             1         2
        // 0..1,2,3,4,5..6..7,8,9..10
//        if (position !in firstAndLast().value && chapter.paragraphs.isNotEmpty()) {
//            (holder as ParagraphHolder).bind(chapter.paragraphs[position - 1].text)
//        } else {
//            (holder as NavigationHolder).bind()
    }
}

const val PARAGRAPH = 1
const val NAVIGATION = 2


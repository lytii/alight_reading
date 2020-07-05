package com.exd.myapplication.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.exd.myapplication.R
import com.exd.myapplication.models.Chapter

class ChapterAdapter : RecyclerView.Adapter<ChapterHolder>() {
    private var chapter: Chapter =
        Chapter(0, "none", "none", "none", "none")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterHolder {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_paragraph, parent, false)
            .let { ChapterHolder(it) }
    }

    fun setChapter(chapter: Chapter) {
        this.chapter = chapter
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = chapter.paragraphs.size

    override fun onBindViewHolder(holder: ChapterHolder, position: Int) {
        holder.bind(chapter.paragraphs[position].text)
    }
}
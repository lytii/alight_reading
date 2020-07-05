package com.exd.myapplication.view

import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exd.myapplication.R

class ChapterHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(paragraph: String) {
        val text = Html.fromHtml(
            paragraph,
            Html.FROM_HTML_MODE_COMPACT
        )
//        val text = paragraph
        view.findViewById<TextView>(R.id.text).text = text
    }
}
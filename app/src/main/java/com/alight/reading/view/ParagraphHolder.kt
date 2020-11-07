package com.alight.reading.view

import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alight.reading.ChapterNavigationListener
import com.alight.reading.R

abstract class ChapterHolder(view: View) : RecyclerView.ViewHolder(view) {

}

open class ParagraphHolder(private val view: View) : ChapterHolder(view) {

    fun bind(paragraph: String) {
        val text = Html.fromHtml(
            paragraph,
            Html.FROM_HTML_MODE_COMPACT
        )
//        val text = paragraph
        view.findViewById<TextView>(R.id.text).text = text
    }
}

class NavigationHolder(
    private val view: View,
    private val listener: ChapterNavigationListener
) : ChapterHolder(view) {

    fun bind() {
        view.findViewById<Button>(R.id.next_button).setOnClickListener { listener.onNext() }
        view.findViewById<Button>(R.id.prev_button).setOnClickListener { listener.onPrev() }
    }
}

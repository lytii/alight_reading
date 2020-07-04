package com.exd.myapplication.view

import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.exd.myapplication.R
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.network.BookApi
import com.exd.myapplication.network.RetrofitBuilder
import com.exd.myapplication.network.parseChapter
import io.reactivex.schedulers.Schedulers

class ChapterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)
        setViewModels()
    }

    private fun setViewModels() {
        val model: ChapterViewModel by viewModels()
        val title = findViewById<TextView>(R.id.title)
        val paragraphList = findViewById<RecyclerView>(R.id.paragraph_list)
        val adapter = ChapterAdapter()
        paragraphList.adapter = adapter
//        paragraphList.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            if(oldScrollY > scrollY) {
//                Log.e("scroll", "up")
//                title.visibility = View.VISIBLE
//            } else {
//                title.visibility = View.GONE
//                Log.e("scroll", "down")
//            }
//        }
        val chapterObserver = Observer<Chapter> { chapter ->
            title.text = Html.fromHtml(chapter.chapterTitle, FROM_HTML_MODE_COMPACT)
            adapter.setChapter(chapter)
        }
        model.chapterData.observe(this, chapterObserver)
        model.setChapter()
    }
}

class ChapterAdapter : RecyclerView.Adapter<ChapterHolder>() {
    private var chapter: Chapter = Chapter(0, "none", "none", "none", "none")

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

class ChapterHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(paragraph: String) {
        val text = Html.fromHtml(paragraph, FROM_HTML_MODE_COMPACT)
//        val text = paragraph
        view.findViewById<TextView>(R.id.text).text = text
    }
}

class ChapterViewModel : ViewModel() {
    val chapterData: MutableLiveData<Chapter> by lazy {
        MutableLiveData<Chapter>()
    }
    private val retrofit = RetrofitBuilder.build()
    private val api = retrofit.create(BookApi::class.java)

    fun setChapter() {
        val chapterUrl =
            "https://honyakusite.wordpress.com/2016/04/27/vendm-016-the-work-of-rebuilding/"
        val chapter = api.getChapter(chapterUrl)
            .subscribeOn(Schedulers.io())
            .blockingGet()
            .parseChapter(chapterUrl)
        chapterData.value = chapter
    }
}

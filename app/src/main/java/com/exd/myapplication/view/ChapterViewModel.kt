package com.exd.myapplication.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exd.myapplication.dagger.ActivityComponent
import com.exd.myapplication.dagger.DaggerChapterViewModelComponent
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.models.Paragraph
import com.exd.myapplication.repo.ChapterRepo
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import javax.inject.Inject

class ChapterViewModel : ViewModel() {
    val TAG = "ChapterViewModel"

    sealed class WebsiteBook(val url: String) {
        abstract fun parseChapterListUrls(doc: Document): List<Chapter>
        abstract fun parseChapter(responseBody: ResponseBody, chapterId: Int): List<Paragraph>

        /** Chapter list order isn't reliable so use in chapter's next/previous chapter links**/
        open var useInChapterNavigation: Boolean = false
        val name = this.javaClass.name

        object VendingMachine : WebsiteBook("https://honyakusite.wordpress.com/vending-machine/") {
            override fun parseChapterListUrls(doc: Document): List<Chapter> {
                return doc.select("ol [href]").mapIndexed { index, chapter ->
                    this.toChapter(index, chapter)
                }
            }

            override fun parseChapter(responseBody: ResponseBody, chapterId: Int): List<Paragraph> {
                val doc = Jsoup.parse(responseBody.string())
                val title = doc.select(".entry-header > h1.entry-title").text()
                // TODO split foot notes from ".entry-content > ol > li"
                val content =
                    doc.select(".entry-content > p, .entry-content > h2, .entry-content > ol > li")
                val navigation = content.removeAt(0)

                val (previous, next) = navigation.select("a[href]").map {
                    val text = it.text()
                    val url = it.attr("href")
                    when {
                        text.contains("previous chapter", ignoreCase = true) -> url
                        text.contains("next chapter", ignoreCase = true) -> url
                        else -> ""
                    }
                }

                return content.mapIndexedNotNull { index, item ->
                    if (item.isNavigation()) {
                        return@mapIndexedNotNull null
                    }

                    val paragraph = item.toString()
                    Paragraph(index, paragraph, chapterId)
                }
            }

            private fun Element.isNavigation(): Boolean {
                return this.children().size > 2 && this.text().let {
                    it.contains("previous chapter", ignoreCase = true)
                            || it.contains("next chapter", ignoreCase = true)
                }
            }

        }
    }

    private fun Disposable.unsaved() {}

    val cachedIndex: Int by lazy { 10 }

    var index = 0

    @Inject
    lateinit var repo: ChapterRepo

    init {
        DaggerChapterViewModelComponent.builder()
            .activityComponent(ActivityComponent.get())
            .build()
            .inject(this)
    }

    private val chapterDataToPushTo = MutableLiveData<Chapter>()
    val chapterDataToBeObserved: LiveData<Chapter> by lazy { chapterDataToPushTo }

    fun loadUrl(chapterUrl: String) {
        repo.getChapter(chapterUrl)
            .doOnSuccess { index = it.index }
            .subscribe { chapter -> chapterDataToPushTo.postValue(chapter) }
            .unsaved()
    }

    fun loadContent(refreshIndex: Boolean = false) {
        val indexSingle = if (refreshIndex) {
            repo.getCachedIndex()
                .doOnSuccess { index = it }
        } else {
            Single.just(index)
        }
        indexSingle
            .flatMap { repo.getChapter(it) }
            .subscribe { chapter -> chapterDataToPushTo.postValue(chapter) }
            .unsaved()
    }


    fun onStrongScrollUp() {
        if (overScrolledUp > 0) {
            Log.e("model", "trigger over scroll up function")
//            if (index > 0) {
//                index--
//                setContent()
//            }
            overScrolledUp = 0
        }
    }

    fun onStrongScrollDown() {
        if (overScrolled > 0) {
            Log.e("model", "trigger over scroll down function")
            overScrolled = 0
            nextChapter()
        }
    }

    fun nextChapter() {
        repo.getChapterList()
            .subscribe { list ->
                if (++index < list.size) {
                    loadContent()
                    repo.saveIndex(index)
                }
            }
            .unsaved()
    }

    fun previousChapter() {
        if (index > 0) {
            index--
            loadContent()
            Completable.fromAction { repo.saveIndex(index) }
                .subscribeOn(Schedulers.io())
                .subscribe().unsaved()
        }
    }

    var overScrolled = 0
    var overScrolledUp = 0

    fun onBottomReached(reached: Boolean) {
        if (!reached) {
            Log.d("model", "resetting overScroll")
            overScrolled = 0
        }
//        else if (overScrolled > 1) {
//            overScrolled = 0
//            if (index < bookData.value?.chapterList?.size ?: 0) {
//                index++
//                setContent()
//            }
//            Log.e("model", "trigger over scroll function")
//        }
        Log.e("model", "onBottomReached: $reached")
    }

    fun onTopReached(reached: Boolean) {
        if (!reached) {
            Log.e("model", "resetting overScroll")
            overScrolledUp = 0
        }
//        else if (overScrolledUp > 1) {
//            overScrolledUp = 0
//            if (index > 0) {
//                index--
//                setContent()
//            }
//            Log.e("model", "trigger over scroll up function")
//        }
        Log.e("model", "onTopReached: $reached")
    }

    fun onOverScroll() {
        overScrolled++
        Log.e("model", "onOverScroll: $overScrolled")
    }

    fun onOverScrollUp() {
        overScrolledUp++
        Log.e("model", "onOverScrollUp: $overScrolledUp")
    }
}

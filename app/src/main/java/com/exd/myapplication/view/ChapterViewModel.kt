package com.exd.myapplication.view

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exd.myapplication.database.BookDB
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.network.BookNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChapterViewModel : ViewModel() {
    val TAG = "ChapterViewModel"

    @Inject
    lateinit var network: BookNetwork

    @Inject
    lateinit var db: BookDB

    init {
        DaggerChapterViewModelComponent.builder()
            .activityComponent(ActivityComponent.get())
            .build()
            .inject(this)
    }

    var chapterData: MediatorLiveData<Chapter> =
        MediatorLiveData<Chapter>()

    fun setChapter(
        chapterUrl: String =
            "https://honyakusite.wordpress.com/2016/04/27/vendm-016-the-work-of-rebuilding/"
    ) {
        Log.i("model", "setting chapter $chapterUrl")
        chapterData.addSource(db.listenToChapter(chapterUrl)) { chapter ->
            Log.e(TAG, "heard chapter $chapter")
            if (chapter == null) {
                network.getChapter(chapterUrl)
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess { db.addChapter(it) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { chapterFromNetwork -> listenToParagraphs(chapterFromNetwork) }
            } else {
                listenToParagraphs(chapter)
            }
        }

    }

    private fun listenToParagraphs(chapter: Chapter) {
        chapterData.addSource(db.listenToParagraphs(chapter.chapterId)) { paragraphs ->
            Log.d(TAG, "heard paragraph size: ${paragraphs.size}")
            if (paragraphs.isNullOrEmpty()) {
                network.getChapter(chapter.chapterUrl)
                    .subscribeOn(Schedulers.io())
                    .subscribe { chapter -> db.addParagraphs(chapter.paragraphs) }
            } else {
                Chapter(
                    chapter.chapterId,
                    chapter.chapterTitle,
                    chapter.chapterUrl
                ).run {
                    this.paragraphs = paragraphs
                    chapterData.value = this
                }
            }
        }
    }

    var overScrolled = 0
    var overScrolledUp = 0

    fun onBottomReached(reached: Boolean) {
        if (!reached) {
            Log.d("model", "resetting overScroll")
            overScrolled = 0
        } else if (overScrolled > 1) {
            overScrolled = 0
            setChapter("https://honyakusite.wordpress.com/2016/05/03/vendm-017-gold-coins-and-silver-coins/")
            Log.e("model", "trigger over scroll function")
        }
        Log.e("model", "onBottomReached: $reached")
    }

    fun onTopReached(reached: Boolean) {
        if (!reached) {
            Log.e("model", "resetting overScroll")
            overScrolledUp = 0
        } else if (overScrolledUp > 1) {
            overScrolledUp = 0
            setChapter()
            Log.e("model", "trigger over scroll up function")
        }
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
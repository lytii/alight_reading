package com.exd.myapplication.view

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exd.myapplication.database.BookDB
import com.exd.myapplication.models.Book
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.network.BookNetwork
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChapterViewModel : ViewModel() {
    val TAG = "ChapterViewModel"

    enum class WebsiteBooks {
        VENDING_MACHINE
    }

    var index = 0

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

    var bookData = MutableLiveData<Book>()

    fun getBook(bookName: String = WebsiteBooks.VENDING_MACHINE.name) {
        Log.d(TAG, "getBook: $bookName")
        val chapterListFromNetwork = network.getChapterList(bookName)
            .map {
                Book(
                    bookId = bookName.hashCode(),
                    bookUrl = "https://honyakusite.wordpress.com/vending-machine/",
                    bookTitle = bookName
                ).apply { chapterList = it }
            }.doOnSuccess { db.addBook(it) }
        db.getBook(bookName)
            .switchIfEmpty(chapterListFromNetwork)
            .flatMap {
                if (it.chapterList.isEmpty()) {
                    chapterListFromNetwork
                } else {
                    Single.just(it)
                }
            }
            .subscribe { book ->
                setChapterContent(book.chapterList[index])
                bookData.postValue(book)
            }
    }

    fun setContent() {
        getBook()
    }

    private fun setChapterContent(chapter: Chapter) {
        Log.i("model", "setting chapter $chapter")
        // chapter paragraphs will always be empty when just from db
        val paragraphs = db.getParagraphs(chapter.chapterId)
        chapter.paragraphs = paragraphs
        if (paragraphs.isNullOrEmpty()) {
            network.getChapter(chapter)
                .doOnSuccess {
                    db.addParagraphs(chapter.chapterId, it.paragraphs)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { chapterFromNetwork ->
                    listenToParagraphs(chapterFromNetwork)
                }
        } else {
            Completable.complete()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { listenToParagraphs(chapter) }

        }
    }

    private fun listenToParagraphs(chapter: Chapter) {
        chapterData.addSource(db.listenToParagraphs(chapter.chapterId)) { paragraphs ->
            Log.d(TAG, "heard paragraph size: ${paragraphs.size}")
            if (paragraphs.isNullOrEmpty()) {
                network.getChapter(chapter)
                    .subscribeOn(Schedulers.io())
                    .subscribe { chapter ->
                        db.addParagraphs(
                            chapter.chapterId,
                            chapter.paragraphs
                        )
                    }
            } else {
                Chapter(
                    chapterId = chapter.chapterId,
                    chapterTitle = chapter.chapterTitle,
                    chapterUrl = chapter.chapterUrl,
                    bookId = chapter.bookId
                ).run {
                    this.paragraphs = paragraphs
                    chapterData.value = this
                }
            }
        }
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
//            if (index < bookData.value?.chapterList?.size ?: 0) {
//                index++
//                setContent()
//            }
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
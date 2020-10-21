package com.exd.myapplication.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exd.myapplication.dagger.ActivityComponent
import com.exd.myapplication.dagger.DaggerChapterViewModelComponent
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.repo.ChapterRepo
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChapterViewModel : ViewModel() {
    val TAG = "ChapterViewModel"

    private fun Disposable.unsaved() {}

    val cachedIndex: Int by lazy { 10 }

    var index = 0

    /**
     * @param addChapter: chapter to add to the scroll list of chapters
     * @param direction: prepend or append to list based on previous or next chapter
     */
    data class ChapterState(
        val addChapter: Chapter,
        val direction: ChapterDirection,
        val scroll: Boolean
    )

    enum class ChapterDirection { PREV, NEXT }

    @Inject
    lateinit var repo: ChapterRepo

    init {
        DaggerChapterViewModelComponent.builder()
            .activityComponent(ActivityComponent.get())
            .build()
            .inject(this)
    }

    private val chapterToPushTo = MutableLiveData<ChapterState>()
    val chapterDataToBeObserved: LiveData<ChapterState> by lazy { chapterToPushTo }

    private fun Chapter.stateNext() = ChapterState(this, ChapterDirection.NEXT, true)
    private fun Chapter.statePrev() = ChapterState(this, ChapterDirection.PREV, true)
    private fun Chapter.asState(direction: ChapterDirection, scroll: Boolean) =
        ChapterState(this, direction, scroll)

    fun loadUrl(chapterUrl: String) {
        Log.e(TAG, "loadUrl: $chapterUrl")
        repo.getChapter(chapterUrl)
            .doOnSuccess { index = it.index }
            .subscribe { chapter ->
                Log.e(TAG, "loadedUrl: $chapterUrl")
                chapterToPushTo.postValue(chapter.asState(ChapterDirection.NEXT, false))
            }
            .unsaved()
    }

    /**
     * When bottom reached, preload next chapter and place it in scroll list
     */
    fun onBottomReached(reached: Boolean) {
        Log.i("cvm", "onBottomReached")
        chapterDataToBeObserved.value?.addChapter?.nextChapterUrl?.let {
            Log.w("cvm", "onBottomReached getting $it")
            repo.getChapter(it)
                .subscribe { chapter ->
                    chapterToPushTo.postValue(
                        chapter.asState(
                            ChapterDirection.NEXT,
                            false
                        )
                    )
                }
                .unsaved()
        }
    }

    fun loadContent(direction: ChapterDirection, refreshIndex: Boolean = false) {
        val indexSingle = if (refreshIndex) {
            repo.getCachedIndex()
                .doOnSuccess { index = it }
        } else {
            Single.just(index)
        }
        indexSingle
            .flatMap { repo.getChapter(it) }
            .subscribe { chapter -> chapterToPushTo.postValue(chapter.asState(direction, true)) }
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
        Log.v(TAG, "nextChapter")
        if (repo.currentBook.useInChapterNavigation) {
            getNextChapterByNavigation()
        } else {
            getNextChapterByIndex()
        }
    }

    private fun getNextChapterByNavigation() {
        chapterDataToBeObserved.value?.addChapter?.nextChapterUrl?.let {
            Log.e(TAG, "getNextChapterByNavigation: $it")
            repo.getChapter(it)
                .subscribe { chapter -> chapterToPushTo.postValue(chapter.stateNext()) }
                .unsaved()
        }
    }

    private fun getNextChapterByIndex() {
        repo.getChapterList()
            .subscribe { list ->
                if (++index < list.size) {
                    loadContent(ChapterDirection.NEXT)
                    repo.saveIndex(index)
                }
            }
            .unsaved()
    }

    fun previousChapter() {
        if (index > 0) {
            index--
            loadContent(ChapterDirection.PREV)
            Completable.fromAction { repo.saveIndex(index) }
                .subscribeOn(Schedulers.io())
                .subscribe().unsaved()
        }
    }

    var overScrolled = 0
    var overScrolledUp = 0

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

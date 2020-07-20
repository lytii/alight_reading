package com.exd.myapplication.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exd.myapplication.dagger.ActivityComponent
import com.exd.myapplication.dagger.DaggerChapterViewModelComponent
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.repo.ChapterRepo
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ChapterViewModel : ViewModel() {
    val TAG = "ChapterViewModel"

    enum class WebsiteBook(val url: String) {
        VENDING_MACHINE("https://honyakusite.wordpress.com/vending-machine/")
    }

    private fun Disposable.unsaved() {}

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

    fun loadContent() {
        repo.getChapter(index)
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

    private fun nextChapter() {
        repo.getChapterList()
            .subscribe { list -> if (++index < list.size) loadContent() }
            .unsaved()
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

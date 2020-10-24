package com.exd.myapplication.ui.chapterlist

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exd.myapplication.dagger.ActivityComponent
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.repo.ChapterRepo
import com.exd.myapplication.view.chapter.TAG
import com.exd.myapplication.view.WebsiteBook
import com.exd.myapplication.dagger.ViewModelScope
import dagger.Component
import javax.inject.Inject

class ChapterListViewModel : ViewModel() {

    @Inject
    lateinit var repo: ChapterRepo

    init {
        DaggerChapterListViewModel_Injector.builder()
            .activityComponent(ActivityComponent.instance)
            .build()
            .inject(this)
    }

    private val chapterList by lazy { MutableLiveData<List<Chapter>>() }
    val chapterData: LiveData<List<Chapter>> by lazy { chapterList }

    @SuppressLint("CheckResult")
    fun getChapterList() {
        repo.getChapterList()
            // todo make this listen to cache, not just single
            .subscribe { list -> chapterList.postValue(list) }
    }

    fun markPreviousAsRead(index: Int) {
        Log.e(TAG, "markPreviousAsRead: $index")
        repo.markPreviousAsRead(book = WebsiteBook.DeathMarch, index = index)
            .subscribe()
    }


    @ViewModelScope
    @Component(dependencies = [ActivityComponent::class])
    interface Injector {
        fun inject(viewModel: ChapterListViewModel)

        fun injector(): Injector
    }
}

package com.exd.myapplication.ui.chapterlist

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exd.myapplication.dagger.ActivityComponent
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.repo.ChapterRepo
import javax.inject.Inject

class ChapterListViewModel : ViewModel() {

    @Inject
    lateinit var repo: ChapterRepo

    init {
        DaggerInjector.builder()
            .activityComponent(ActivityComponent.instance)
            .build()
            .inject(this)
    }

    private val chapterList by lazy { MutableLiveData<List<Chapter>>() }
    val chapterData: LiveData<List<Chapter>> by lazy { chapterList }

    @SuppressLint("CheckResult")
    fun getChapterList() {
        repo.getChapterList()
            .subscribe { list -> chapterList.postValue(list) }

    }

}
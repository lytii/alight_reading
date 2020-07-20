package com.exd.myapplication.dagger

import com.exd.myapplication.view.ChapterViewModel
import dagger.Component

@Component(dependencies = [ActivityComponent::class])
interface ChapterViewModelComponent {
    fun inject(vm: ChapterViewModel)

    fun activityComponent(): ChapterViewModelComponent
}
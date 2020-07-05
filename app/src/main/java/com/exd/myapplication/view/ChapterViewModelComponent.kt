package com.exd.myapplication.view

import dagger.Component

@Component(dependencies = [ActivityComponent::class])
interface ChapterViewModelComponent {
    fun inject(vm: ChapterViewModel)

    fun activityComponent(): ChapterViewModelComponent
}
package com.exd.myapplication.view.chapter

import com.exd.myapplication.dagger.ActivityComponent
import com.exd.myapplication.dagger.ViewModelScope
import dagger.Component

@ViewModelScope
@Component(dependencies = [ActivityComponent::class])
interface Injector {
    fun inject(vm: ChapterViewModel)

    fun build(): Injector
}

package com.alight.reading.view.chapter

import com.alight.reading.dagger.ActivityComponent
import com.alight.reading.dagger.ViewModelScope
import dagger.Component

@ViewModelScope
@Component(dependencies = [ActivityComponent::class])
interface Injector {
    fun inject(vm: ChapterViewModel)

    fun build(): Injector
}

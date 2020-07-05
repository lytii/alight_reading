package com.exd.myapplication.view

import android.content.Context
import dagger.BindsInstance
import dagger.Component

@Component
abstract class ActivityComponent {
    companion object {
        lateinit var instance: ActivityComponent

        fun get() = instance
    }

    abstract fun context(): Context
    abstract fun inject(activity: ChapterActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun bindContext(context: Context): Builder

        fun build(): ActivityComponent
    }
}
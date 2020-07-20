package com.exd.myapplication.dagger

import android.content.Context
import com.exd.myapplication.view.ChapterActivity
import dagger.BindsInstance
import dagger.Component

@Component
interface ActivityComponent {
    companion object {
        lateinit var instance: ActivityComponent

        fun get() =
            instance
    }

    fun context(): Context
    fun inject(activity: ChapterActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun bindContext(context: Context): Builder

        fun build(): ActivityComponent
    }
}
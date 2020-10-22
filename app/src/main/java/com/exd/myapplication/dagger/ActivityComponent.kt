package com.exd.myapplication.dagger

import android.content.Context
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

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun bindContext(context: Context): Builder

        fun build(): ActivityComponent
    }
}
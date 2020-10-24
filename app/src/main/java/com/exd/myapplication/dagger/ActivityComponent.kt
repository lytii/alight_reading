package com.exd.myapplication.dagger

import android.content.Context
import com.exd.myapplication.repo.ChapterRepo
import dagger.BindsInstance
import dagger.Component
import java.lang.IllegalStateException
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface ActivityComponent {
    companion object {
        lateinit var instance: ActivityComponent

        fun get() = instance

        fun buildDagger(context: Context) {
            if (this::instance.isInitialized) throw IllegalStateException("re-initting ActivityComponent")

            instance = DaggerActivityComponent.builder()
                .bindContext(context)
                .build()
        }
    }

    fun context(): Context
    fun chapterRepo(): ChapterRepo

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun bindContext(context: Context): Builder

        fun build(): ActivityComponent
    }
}

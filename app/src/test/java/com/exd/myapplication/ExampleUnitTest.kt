package com.exd.myapplication

import com.exd.myapplication.network.BookApi
import com.exd.myapplication.network.RetrofitBuilder
import com.exd.myapplication.network.parseBooks
import com.exd.myapplication.network.parseChapter
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    private val retrofit = RetrofitBuilder.build()
    private val api = retrofit.create(BookApi::class.java)

    @Test
    fun getList() {
        val get = api.getChapterList().blockingGet()
        parseBooks(get).printLn()
    }

    @Test
    fun getChapter() {
        val chapterUrl =
            "https://honyakusite.wordpress.com/2016/04/27/vendm-016-the-work-of-rebuilding/"
        val chapter = api.getChapter(chapterUrl).blockingGet()
            .parseChapter(chapterUrl)
            .printLn()
    }

    fun Any.printLn() = println(this)
}
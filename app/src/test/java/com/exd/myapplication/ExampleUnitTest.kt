package com.exd.myapplication

import com.exd.myapplication.network.BookApi
import com.exd.myapplication.network.parseChapterList
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

    @Test
    fun getList() {
//        val get = api.getChapterList().blockingGet()
//        parseChapterList(get).printLn()
    }

    @Test
    fun getChapter() {
//        val chapterUrl =
//            "https://honyakusite.wordpress.com/2016/04/27/vendm-016-the-work-of-rebuilding/"
//        val response = api.getChapter(chapterUrl).blockingGet()
//        parseChapter(response, chapterUrl)
//            .printLn()
    }

    fun Any.printLn() = println(this)
}
package com.exd.myapplication

import com.exd.myapplication.network.BookNetwork
import com.exd.myapplication.view.WebsiteBook
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test

class WebsiteBookTest {

    private val bookNetwork = BookNetwork()
    private val book = WebsiteBook.DeathMarch
    private val chapterUrl =
        "https://www.sousetsuka.com/2015/01/death-march-kara-hajimaru-isekai_16.html"

    @Test
    fun parseDeathMarch() {
        bookNetwork.getChapterList(book)
            .blockingGet()
            .printLn()
    }

    @Test
    fun parseDeathMarchChapter() {
        val chapter = bookNetwork.getChapterList(book).blockingGet()[6]
        bookNetwork.getChapter(chapter, book)
            .blockingGet()
            .printLn()
    }

}
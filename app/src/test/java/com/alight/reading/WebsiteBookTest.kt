package com.alight.reading

import com.alight.reading.network.BookNetwork
import com.alight.reading.view.WebsiteBook
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
            .println()
    }

    @Test
    fun parseDeathMarchChapter() {
        val chapter = bookNetwork.getChapterList(book).blockingGet()[6]
        bookNetwork.getChapter(chapter, book)
            .blockingGet()
            .println()
    }

}
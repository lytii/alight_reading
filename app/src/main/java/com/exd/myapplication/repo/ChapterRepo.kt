package com.exd.myapplication.repo

import com.exd.myapplication.database.BookDB
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.models.Paragraph
import com.exd.myapplication.network.BookNetwork
import com.exd.myapplication.view.ChapterViewModel.WebsiteBook
import com.exd.myapplication.view.ChapterViewModel.WebsiteBook.VENDING_MACHINE
import io.reactivex.Single
import javax.inject.Inject

class ChapterRepo @Inject constructor(
    private val network: BookNetwork,
    private val cache: BookDB
) {

    fun getCachedIndex(): Single<Int> {
        return cache.getIndex().map { it.index }
    }

    fun saveIndex(index: Int) {
        cache.saveIndex(index)
    }

    fun getChapter(chapterIndex: Int): Single<Chapter> {
        return getChapterList()
            .map { it[chapterIndex] }
            .flatMap(this::getChapterFromNetworkOrCache)
    }

    fun getChapter(url: String): Single<Chapter> {
        return getChapterList()
            .map { it.first { chapter -> chapter.chapterUrl == url } }
            .flatMap(this::getChapterFromNetworkOrCache)
    }

    private fun getChapterFromNetworkOrCache(chapter: Chapter): Single<Chapter> {
        return Single.just(chapter)
            .filter { it.paragraphs.isNotEmpty() }
            .switchIfEmpty(
                getParagraphs(chapter.chapterUrl)
                    .map { chapter.apply { this.paragraphs = it } }
            )
    }

    private fun getParagraphs(chapterUrl: String): Single<List<Paragraph>> {
        val fromNetwork: Single<List<Paragraph>> = network.getChapter(chapterUrl)
            .doOnSuccess { cache.saveParagraphs(chapterUrl.hashCode(), it) }
        // get paragraphs from cache
        return cache.getParagraphMaybe(chapterUrl)
            // if empty get paragraphs from network
            .switchIfEmpty(fromNetwork)
    }

    fun getChapterList(book: WebsiteBook = VENDING_MACHINE): Single<List<Chapter>> {
        val listFromNetwork = network.getChapterList(book)
            .doAfterSuccess { cache.saveChapterList(it) }
        return cache.getChapterList(book.name)
            .switchIfEmpty(listFromNetwork)
    }
}

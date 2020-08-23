package com.exd.myapplication.repo

import com.exd.myapplication.database.BookDB
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.network.BookNetwork
import com.exd.myapplication.view.WebsiteBook
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
            .switchIfEmpty(getParagraphs(chapter))
    }

    private fun getParagraphs(chapter: Chapter): Single<Chapter> {
        val fromNetwork: Single<Chapter> = network.getChapter(chapter)
            .doOnSuccess { cache.saveParagraphs(it.paragraphs) }
        // get paragraphs from cache
        return cache.getParagraphMaybe(chapter.chapterUrl)
            .map { chapter.apply { paragraphs = it } }
            // if empty get paragraphs from network
            .switchIfEmpty(fromNetwork)
    }

    fun getChapterList(book: WebsiteBook = WebsiteBook.VendingMachine): Single<List<Chapter>> {
        val listFromNetwork = network.getChapterList(book)
            .doAfterSuccess { cache.saveChapterList(it) }
        return cache.getChapterList(book.name)
            .switchIfEmpty(listFromNetwork)
    }
}

package com.alight.reading.repo

import android.util.Log
import com.alight.reading.database.BookDB
import com.alight.reading.models.Chapter
import com.alight.reading.network.BookNetwork
import com.alight.reading.view.chapter.TAG
import com.alight.reading.view.WebsiteBook
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class ChapterRepo @Inject constructor(
    private val network: BookNetwork,
    private val cache: BookDB
) {

    // for testing?
    val currentBook: WebsiteBook = WebsiteBook.DeathMarch

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
        return cache.getChapter(url)
            .doOnSuccess { Log.d(TAG, "gotChapter: cache") }
            .flatMap(this::getChapterFromNetworkOrCache)
    }

    private fun getChapterFromNetworkOrCache(chapter: Chapter): Single<Chapter> {
        return Single.just(chapter)
            .filter { it.paragraphs.isNotEmpty() }
            .switchIfEmpty(getParagraphs(chapter))
    }

    private fun getParagraphs(chapter: Chapter): Single<Chapter> {
        val fromNetwork: Single<Chapter> = network.getChapter(chapter)
            .doOnSuccess { cache.saveChapter(chapter) }
        // get paragraphs from cache
        return cache.getParagraphMaybe(chapter.chapterUrl)
            .doOnSuccess { Log.d(TAG, "getParagraphs: cache") }
            .map { chapter.apply { paragraphs = it } }
            // if empty get paragraphs from network
            .switchIfEmpty(fromNetwork)
    }

    fun getChapterList(book: WebsiteBook = WebsiteBook.DeathMarch): Single<List<Chapter>> {
        val listFromNetwork = network.getChapterList(book)
            .doAfterSuccess { cache.saveChapterList(it) }
        return cache.getChapterList(book.name)
            .switchIfEmpty(listFromNetwork)
    }

    fun markPreviousAsRead(book: WebsiteBook = WebsiteBook.DeathMarch, index: Int): Completable {
        Log.e(TAG, "markPreviousAsRead: $index")
        return cache.markPreviousAsCached(book.name, index)
    }
}

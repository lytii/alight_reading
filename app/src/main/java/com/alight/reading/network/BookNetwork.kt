package com.alight.reading.network

import com.alight.reading.models.Chapter
import com.alight.reading.view.WebsiteBook
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.http.GET
import retrofit2.http.Url
import javax.inject.Inject
import javax.inject.Singleton

interface BookApi {
    @GET("https://honyakusite.wordpress.com/vending-machine")
    fun getChapterList(): Single<ResponseBody>

    @GET
    fun getChapter(@Url chapterUrl: String): Single<ResponseBody>
}

@Singleton
class BookNetwork @Inject constructor(private val api: BookApi) {
    private lateinit var book: WebsiteBook

    /**
     * Get
     */
    fun getChapter(
        chapter: Chapter,
        book: WebsiteBook? = null
    ): Single<Chapter> = api.getChapter(chapter.chapterUrl)
        .subscribeOn(Schedulers.io())
        .map { (book ?: this.book).parseChapter(it, chapter) }

    fun getChapterList(book: WebsiteBook): Single<List<Chapter>> {
        this.book = book
        return api.getChapter(book.url)
            .subscribeOn(Schedulers.io())
            .map { Jsoup.parse(it.string()) }
            .map { book.parseChapterListUrls(it) }
    }
}

var time = 0L
fun now() = System.currentTimeMillis()

fun diff() {
    val now = now()
    if (time == 0L) {
        time = now
        return
    }
    println(now - time)
    time = now
}

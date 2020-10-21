package com.exd.myapplication.network

import com.exd.myapplication.models.Chapter
import com.exd.myapplication.models.Paragraph
import com.exd.myapplication.view.ChapterViewModel
import com.exd.myapplication.view.WebsiteBook
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import javax.inject.Inject

interface BookApi {
    @GET("https://honyakusite.wordpress.com/vending-machine")
    fun getChapterList(): Single<ResponseBody>

    @GET
    fun getChapter(@Url chapterUrl: String): Single<ResponseBody>
}

abstract class Network {
    companion object {
        private const val baseUrl = "https://honyakusite.wordpress.com/vending-machine/"
    }

    private val logging =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit: Retrofit by lazy {
        val gson = GsonBuilder().setLenient().create()
        Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}

class BookNetwork @Inject constructor() : Network() {
    private val api = retrofit.create(BookApi::class.java)
    private val book: WebsiteBook = WebsiteBook.DeathMarch

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
//        this.book = book
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

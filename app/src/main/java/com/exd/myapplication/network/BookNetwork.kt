package com.exd.myapplication.network

import com.exd.myapplication.models.Chapter
import com.exd.myapplication.models.Paragraph
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

    /**
     * Get
     */
    fun getChapter(url: String): Single<List<Paragraph>> = api.getChapter(url)
        .subscribeOn(Schedulers.io())
        .map { parseChapter(it, url.hashCode()) }

    fun getChapterList(bookId: String): Single<List<Chapter>> = api.getChapterList()
        .subscribeOn(Schedulers.io())
        .map { parseChapterList(bookId, it) }
}


fun parseChapterList(bookId: String, body: ResponseBody): List<Chapter> {
    val doc = Jsoup.parse(body.string())
    return doc.select("ol [href]").mapIndexed { index, chapter ->
        val url = chapter.attr("href")
        Chapter(
            chapterId = url.hashCode(),
            chapterTitle = chapter.text(),
            chapterUrl = url,
            bookId = bookId.hashCode(),
            index = index
        )
    }
}

fun parseChapter(responseBody: ResponseBody, chapterId: Int): List<Paragraph> {
    val doc = Jsoup.parse(responseBody.string())
    val title = doc.select(".entry-header > h1.entry-title").text()
    // TODO split foot notes from ".entry-content > ol > li"
    val content = doc.select(".entry-content > p, .entry-content > h2, .entry-content > ol > li")
    val navigation = content.removeAt(0)

    val (previous, next) = navigation.select("a[href]").map {
        val text = it.text()
        val url = it.attr("href")
        when {
            text.contains("previous chapter", ignoreCase = true) -> url
            text.contains("next chapter", ignoreCase = true) -> url
            else -> ""
        }
    }

    return content.mapIndexedNotNull { index, item ->
        if (item.isNavigation()) {
            return@mapIndexedNotNull null
        }

        val paragraph = item.toString()
        Paragraph(index, paragraph, chapterId)
    }
}

private fun Element.isNavigation(): Boolean {
    return this.children().size > 2 && this.text().let {
        it.contains("previous chapter", ignoreCase = true)
                || it.contains("next chapter", ignoreCase = true)
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

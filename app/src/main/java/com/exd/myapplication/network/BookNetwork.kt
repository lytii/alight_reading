package com.exd.myapplication.network

import com.exd.myapplication.models.Chapter
import com.exd.myapplication.models.Paragraph
import com.google.gson.GsonBuilder
import io.reactivex.Single
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import kotlin.random.Random

interface BookApi {
    @GET("https://honyakusite.wordpress.com/vending-machine")
    fun getChapterList(): Single<ResponseBody>

    @GET
    fun getChapter(@Url chapterUrl: String): Single<ResponseBody>
}

object RetrofitBuilder {
    private const val baseUrl = "https://honyakusite.wordpress.com/vending-machine/"

    fun build(): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}


fun parseBooks(body: ResponseBody): List<Chapter> {
    val doc = Jsoup.parse(body.string())
    return doc.select("ol [href]").map {
        Chapter(
            chapterId = it.text().hashCode(),
            chapterTitle = it.text(),
            chapterUrl = it.attr("href")
        )
    }
}

fun ResponseBody.parseChapter(chapterUrl: String): Chapter {
    val doc = Jsoup.parse(this.string())
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

    val chapterId = title.hashCode()

    val paragraphs = content.eachText().mapIndexed { index, paragraph ->
        Paragraph(index, paragraph, chapterId)
    }

    return Chapter(
        chapterId = chapterId,
        chapterTitle = title,
        chapterUrl = chapterUrl,
        previousUrl = previous,
        nextUrl = next
    ).apply {
        this.paragraphs = paragraphs
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
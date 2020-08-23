package com.exd.myapplication.view

import com.exd.myapplication.models.Chapter
import com.exd.myapplication.models.Paragraph
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements

sealed class WebsiteBook(val url: String) {
    abstract fun parseChapterListUrls(doc: Document): List<Chapter>
    abstract fun parseChapter(responseBody: ResponseBody, chapterId: Int): List<Paragraph>

    /** Chapter list order isn't reliable so use in chapter's next/previous chapter links**/
    open var useInChapterNavigation: Boolean = false
    val name = this.javaClass.name

    object VendingMachine : WebsiteBook("https://honyakusite.wordpress.com/vending-machine/") {
        override fun parseChapterListUrls(doc: Document): List<Chapter> {
            return doc.select("ol [href]").mapIndexed { index, chapter ->
                this.toChapter(index, chapter)
            }
        }

        override fun parseChapter(responseBody: ResponseBody, chapterId: Int): List<Paragraph> {
            val doc = Jsoup.parse(responseBody.string())
            val title = doc.select(".entry-header > h1.entry-title").text()
            // TODO split foot notes from ".entry-content > ol > li"
            val content =
                doc.select(".entry-content > p, .entry-content > h2, .entry-content > ol > li")
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
                Paragraph(
                    index,
                    paragraph,
                    chapterId
                )
            }
        }

        private fun Element.isNavigation(): Boolean {
            return this.children().size > 2 && this.text().let {
                it.contains("previous chapter", ignoreCase = true)
                        || it.contains("next chapter", ignoreCase = true)
            }
        }
    }

    protected fun WebsiteBook.toChapter(index: Int, element: Element): Chapter {
        val url = element.attr("href")
        return Chapter(
            chapterId = url.hashCode(),
            chapterTitle = element.text(),
            chapterUrl = url,
            bookId = name.hashCode(),
            index = index
        )
    }
}

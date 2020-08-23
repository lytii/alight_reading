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
    abstract fun parseChapter(responseBody: ResponseBody, chapter: Chapter): Chapter

    /** Chapter list order isn't reliable so use in chapter's next/previous chapter links**/
    open var useInChapterNavigation: Boolean = false
    val name = this.javaClass.name

    object VendingMachine : WebsiteBook("https://honyakusite.wordpress.com/vending-machine/") {
        override fun parseChapterListUrls(doc: Document): List<Chapter> {
            return doc.select("ol [href]").mapIndexed { index, chapter ->
                this.toChapter(index, chapter)
            }
        }

        override fun parseChapter(responseBody: ResponseBody, chapter: Chapter): Chapter {
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

            val paragraphList = content.mapIndexedNotNull { index, item ->
                if (item.isNavigation()) {
                    return@mapIndexedNotNull null
                }

                val paragraph = item.toString()
                Paragraph(
                    index,
                    paragraph,
                    chapter.chapterUrl.hashCode()
                )
            }

            chapter.paragraphs = paragraphList
            return chapter
        }

        private fun Element.isNavigation(): Boolean {
            return this.children().size > 2 && this.text().let {
                it.contains("previous chapter", ignoreCase = true)
                        || it.contains("next chapter", ignoreCase = true)
            }
        }
    }

    object DeathMarch : WebsiteBook("https://www.sousetsuka.com/p/blog-page_15.html") {
        override var useInChapterNavigation: Boolean = true


        override fun parseChapter(responseBody: ResponseBody, chapter: Chapter): Chapter {
            val doc = Jsoup.parse(responseBody.string())
            val content = doc.select(".entry-content")

            val nav = content.select("a[href]")
            var prev: String? = null
            var next: String? = null


            val paragraphList = content.first()
                .childNodes()
                .filter { !it.isLineBreak() }
                .mapIndexedNotNull { index, node ->
                    if (node is Element) {
                        val linkText = node.select("a[href]")
                        when {
                            linkText.text().contains("Previous Chapter", ignoreCase = true) -> {
                                prev = linkText.attr("href")
                                return@mapIndexedNotNull null
                            }
                            linkText.text().contains("Next Chapter", ignoreCase = true) -> {
                                next = linkText.attr("href")
                                return@mapIndexedNotNull null
                            }
                            node.getElementsByTag("script").isNotEmpty() ->
                                return@mapIndexedNotNull null
                        }
                    }
                    val paragraph = node.toString()
                    Paragraph(
                        index,
                        paragraph,
                        chapter.chapterId
                    )
                }
            return chapter.apply {
                paragraphs = paragraphList
                prevChapterUrl = prev
                nextChapterUrl = next
            }
        }

        override fun parseChapterListUrls(doc: Document): List<Chapter> {
            return doc.select(".entry-content")
                .map { it.select("a[href]") }
                .first()
                .mapIndexed { index, element -> this.toChapter(index, element) }
        }

        private fun Node.isLineBreak(): Boolean =
            this is Element && this.tagName() == "br"

        private fun Node.isNewLine(): Boolean =
            this is TextNode && this.wholeText == "\n"

        private fun Elements.getPreviousUrl(): String? =
            this.firstOrNull { it.text().contains("Previous Chapter") }?.attr("href")

        private fun Elements.getNextUrl(): String? =
            this.firstOrNull { it.text().contains("Next Chapter") }?.attr("href")
    }

    protected fun WebsiteBook.toChapter(index: Int, element: Element): Chapter {
        val url = element.attr("href")
        return Chapter(
            chapterTitle = element.text(),
            chapterUrl = url,
            bookId = name.hashCode(),
            index = index
        )
    }
}
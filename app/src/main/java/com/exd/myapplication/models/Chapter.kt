package com.exd.myapplication.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Book(
    @PrimaryKey(autoGenerate = true)
    val bookId: Int,
    val bookTitle: String,
    val bookUrl: String
) {
    @Ignore
    var chapterList: List<Chapter> = emptyList()
}

@Entity
data class Chapter(
    @PrimaryKey
    val chapterId: Int,
    val chapterTitle: String,
    val chapterUrl: String,
    val bookId: Int,
    val index: Int
) {

    companion object {
        val emptyChapter = Chapter(0, "", "", 0, 0)
    }

    @Ignore
    var paragraphs: List<Paragraph> = emptyList()

    override fun toString(): String {
        return "Chapter(chapterId=$chapterId, " +
                "chapterTitle='$chapterTitle', " +
                "chapterUrl='$chapterUrl', " +
                "paragraphs=${paragraphs.map { '\n' + it.text }}, "
    }
}

@Entity
data class Bookmark(val index: Int, @PrimaryKey val key: Int = 0) {
    companion object {
        val starting = Bookmark(50)
    }
}

@Entity(primaryKeys = ["index", "chapterId"])
data class Paragraph constructor(
    val index: Int,
    val text: String,
    val chapterId: Int
) {}

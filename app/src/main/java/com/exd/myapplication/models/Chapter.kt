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
}

@Entity
data class Chapter(
    @PrimaryKey
    val chapterId: Int,
    val chapterTitle: String,
    val chapterUrl: String,
    val previousUrl: String = "",
    val nextUrl: String = ""
) {

    @Ignore
    var paragraphs: List<Paragraph> = emptyList()

    override fun toString(): String {
        return "Chapter(chapterId=$chapterId, " +
                "chapterTitle='$chapterTitle', " +
                "chapterUrl='$chapterUrl', " +
                "paragraphs=${paragraphs.map { '\n' + it.text }}, " +
                "previousUrl='$previousUrl', " +
                "nextUrl='$nextUrl')"
    }
}

@Entity(primaryKeys = ["index", "chapterId"])
data class Paragraph(
    val index: Int,
    val text: String,
    val chapterId: Int
)

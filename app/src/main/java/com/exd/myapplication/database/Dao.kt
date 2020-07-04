package com.exd.myapplication.database

import androidx.room.*
import com.exd.myapplication.models.Book
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.models.Paragraph

@Dao
interface BookDao {

    @Query("SELECT * FROM Book where (:id) == bookId")
    fun getBook(id: Int): Book

    @Query("SELECT * FROM Book")
    fun getBooks(): List<Book>

    @Insert
    fun addBook(book: Book)

    @Insert
    fun addParagraphs(paragraphs: List<Paragraph>)

    @Query("SELECT * FROM Paragraph where (:chapterId) == chapterId")
    fun getParagraphs(chapterId: Int): List<Paragraph>
}


@Database(entities = [Book::class, Chapter::class, Paragraph::class], version = 1)
abstract class BookDB : RoomDatabase() {
    abstract fun bookDao(): BookDao
}
package com.exd.myapplication.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.exd.myapplication.models.Book
import com.exd.myapplication.models.Chapter
import com.exd.myapplication.models.Paragraph
import javax.inject.Inject

@Dao
interface BookDao {

    @Query("SELECT * FROM Book where (:id) == bookId")
    fun getBook(id: Int): Book

    @Query("SELECT * FROM Book")
    fun getBooks(): List<Book>

    @Insert(onConflict = REPLACE)
    fun addBook(book: Book)

    @Insert(onConflict = REPLACE)
    fun addParagraphs(paragraphs: List<Paragraph>)

    @Insert(onConflict = REPLACE)
    fun addChapter(chapter: Chapter)

    @Query("SELECT * FROM Chapter where (:chapterId) == chapterId")
    fun getChapter(chapterId: Int): Chapter

    @Query("SELECT * FROM Paragraph where (:chapterId) == chapterId")
    fun getParagraphs(chapterId: Int): List<Paragraph>

    @Query("SELECT * FROM Paragraph where (:chapterId) LIKE chapterId")
    fun listenToParagraphs(chapterId: Int): LiveData<List<Paragraph>>

    @Query("SELECT * FROM Chapter where (:url) LIKE chapterUrl")
    fun listenToChapter(url: String): LiveData<Chapter>

}


@Database(
    entities = [Book::class, Chapter::class, Paragraph::class],
    version = 1,
    exportSchema = false
)
abstract class BookRoomDB : RoomDatabase() {
    abstract fun bookDao(): BookDao
}

class BookDB @Inject constructor(context: Context) {
    private val db = Room.databaseBuilder(context, BookRoomDB::class.java, "books")
        .fallbackToDestructiveMigration()
        .build()

    private val dao = db.bookDao()

    fun addChapter(chapter: Chapter) {
        return dao.addChapter(chapter)
    }

    fun addParagraphs(paragraphs: List<Paragraph>) {
        return dao.addParagraphs(paragraphs)
    }


    fun listenToParagraphs(chapterId: Int): LiveData<List<Paragraph>> {
        return dao.listenToParagraphs(chapterId)
    }

    fun listenToChapter(url: String): LiveData<Chapter> {
        return dao.listenToChapter(url)
    }
}

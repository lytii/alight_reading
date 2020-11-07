package com.alight.reading.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.alight.reading.models.Book
import com.alight.reading.models.Bookmark
import com.alight.reading.models.Chapter
import com.alight.reading.models.Paragraph
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Dao
interface BookDao {

    @Query("SELECT * FROM Book where (:id) == bookId")
    fun getBook(id: Int): Book

    @Query("SELECT * FROM Book")
    fun getBooks(): List<Book>

    @Insert(onConflict = REPLACE)
    fun addBook(book: Book)

    @Insert(onConflict = REPLACE)
    fun addChapter(chapter: Chapter)

    @Query("SELECT * FROM Chapter where (:chapterId) == chapterId")
    fun getChapter(chapterId: Int): Chapter

    @Query("SELECT * FROM Chapter where (:url) LIKE chapterUrl")
    fun getChapter(url: String): Chapter

    @Query("SELECT * FROM Chapter where (:url) LIKE chapterUrl")
    fun listenToChapter(url: String): LiveData<Chapter>

    @Insert(onConflict = REPLACE)
    fun addParagraphs(paragraphs: List<Paragraph>)

    @Query("SELECT * FROM Paragraph where (:chapterId) == chapterId")
    fun getParagraphs(chapterId: Int): List<Paragraph>

    @Query("SELECT * FROM Paragraph where (:chapterId) LIKE chapterId")
    fun listenToParagraphs(chapterId: Int): LiveData<List<Paragraph>>

    @Insert(onConflict = REPLACE)
    fun saveChapterList(list: List<Chapter>)

    @Query("SELECT * FROM Chapter where (:id) == bookId")
    fun getChapterList(id: Int): List<Chapter>

    @Query("SELECT * FROM Chapter where (:id) == bookId AND (:currentIndex) >= `index`")
    fun getPreviousChapters(id: Int, currentIndex: Int): List<Chapter>

    @Query("SELECT * FROM Bookmark")
    fun getIndex(): Bookmark

    @Insert(onConflict = REPLACE)
    fun saveIndex(bookmark: Bookmark)

}


@Database(
    entities = [Book::class, Chapter::class, Paragraph::class, Bookmark::class],
    version = 1,
    exportSchema = false
)
abstract class BookRoomDB : RoomDatabase() {
    abstract fun bookDao(): BookDao
}

@Singleton
class BookDB @Inject constructor(context: Context) {
    val TAG = "DB"
    private val db = Room.databaseBuilder(context, BookRoomDB::class.java, "books")
        .fallbackToDestructiveMigration()
        .build()

    private val dao = db.bookDao()

    fun addBook(book: Book) {
        dao.addBook(book)
    }

    fun getBook(name: String): Maybe<Book> {
        return Maybe.fromCallable { dao.getBook(name.hashCode()) }
            .subscribeOn(Schedulers.io())
    }

    fun getIndex(): Single<Bookmark> {
        return Maybe.fromCallable { dao.getIndex() }
            .subscribeOn(Schedulers.io())
            .toSingle(Bookmark.starting)
    }

    fun saveIndex(index: Int) {
        dao.saveIndex(Bookmark(index))
    }

    fun saveChapterList(list: List<Chapter>) {
        dao.saveChapterList(list)
    }

    fun getChapterList(bookName: String): Maybe<List<Chapter>> {
        return Maybe.fromCallable {
            dao.getChapterList(bookName.hashCode()).sortedBy { it.index }
        }
            .filter { it.isNotEmpty() }
            .subscribeOn(Schedulers.io())
    }

    fun markPreviousAsCached(bookName: String, index: Int): Completable {
        return Maybe.fromCallable {
            dao.getPreviousChapters(bookName.hashCode(), index)
                .also { list ->
                    list.forEach { it.isCached = true }
                    dao.saveChapterList(list)
                }

        }
            .ignoreElement()
            .subscribeOn(Schedulers.io())
    }

    fun saveChapter(chapter: Chapter) {
        Log.v(TAG, "addChapter: $chapter")
        chapter.isCached = true
        dao.addChapter(chapter)
        if (chapter.paragraphs.isNotEmpty()) {
            dao.addParagraphs(chapter.paragraphs)
        }
    }

    fun getChapter(url: String): Single<Chapter> {
        return Single.fromCallable { dao.getChapter(url) }
            .subscribeOn(Schedulers.io())
            .flatMap { chapter ->
                Log.d(TAG, "gotChapter: from dao")
                getParagraphMaybe(url)
                    .map {
                        Log.d(TAG, "gotParagraphs: from dao")
                        chapter.apply { paragraphs = it }
                    }
                    .toSingle(chapter)
            }
    }

    fun getParagraphMaybe(chapterUrl: String): Maybe<List<Paragraph>> {
        return Single.fromCallable { getParagraphs(chapterUrl.hashCode()) }
            .filter { it.isNotEmpty() }
            .subscribeOn(Schedulers.io())
    }

    private fun getParagraphs(chapterId: Int): List<Paragraph> {
        return dao.getParagraphs(chapterId)
    }

    fun saveParagraphs(paragraphs: List<Paragraph>) {
        Log.i(TAG, "addParagraphs: ${paragraphs.first().chapterId}")
        return dao.addParagraphs(paragraphs)
    }

    fun listenToParagraphs(chapterId: Int): LiveData<List<Paragraph>> {
        return dao.listenToParagraphs(chapterId)
    }

    fun listenToChapter(url: String): LiveData<Chapter> {
        return dao.listenToChapter(url)
    }
}

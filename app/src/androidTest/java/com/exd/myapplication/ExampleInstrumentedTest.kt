package com.exd.myapplication

import android.util.Log
import android.util.Range
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.exd.myapplication.database.BookDB
import com.exd.myapplication.models.Book
import com.exd.myapplication.models.Paragraph

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import kotlin.random.Random

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.exd.myapplication", appContext.packageName)
    }

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val dbBuilder = Room.databaseBuilder(appContext, BookDB::class.java, "myBooks")
        .fallbackToDestructiveMigration()
        .build()
    val db = dbBuilder.bookDao()

    @Before
    fun setup() {
        dbBuilder.clearAllTables()
    }

    @Test
    fun buildDB() {
        val book = Book(1, "title", "url")
        db.addBook(book)
        db.getBook(1).println()
    }

    @Test
    fun savingParagraphs() {
        val chapterId = 25
        val paragraphs = (1..5).map {
            Paragraph(it, "Paragraph $it", chapterId)
        }
        db.addParagraphs(paragraphs)
        db.getParagraphs(chapterId).println()
    }

    fun Any.println() = Log.e("TestSystemOut", this.toString())
}

package com.exd.myapplication.view.chapter

import com.exd.myapplication.models.Chapter

/**
 * @param addChapter: chapter to add to the scroll list of chapters
 * @param direction: prepend or append to list based on previous or next chapter
 */
data class ChapterState(
    val addChapter: Chapter,
    val direction: ChapterDirection,
    val scroll: Boolean
)


internal fun Chapter.stateNext() = ChapterState(this, ChapterDirection.NEXT, true)

internal fun Chapter.statePrev() = ChapterState(this, ChapterDirection.PREV, true)

internal fun Chapter.asState(direction: ChapterDirection, scroll: Boolean) =
    ChapterState(this, direction, scroll)

enum class ChapterDirection { PREV, NEXT }
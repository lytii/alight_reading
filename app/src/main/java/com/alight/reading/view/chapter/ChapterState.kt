package com.alight.reading.view.chapter

import com.alight.reading.models.Chapter

/**
 * @param addChapter: chapter to add to the scroll list of chapters
 * @param direction: prepend or append to list based on previous or next chapter
 */
data class ChapterState(
    val addChapter: Chapter,
    val direction: ChapterDirection,
    val scroll: Boolean,
    val scrollPos: Int
)


internal fun Chapter.stateNext() = ChapterState(this, ChapterDirection.NEXT, true, 0)

internal fun Chapter.statePrev() = ChapterState(this, ChapterDirection.PREV, true, 0)

internal fun Chapter.asState(direction: ChapterDirection, scroll: Boolean, pos: Int = 0) =
    ChapterState(this, direction, scroll, pos)

enum class ChapterDirection { PREV, NEXT }
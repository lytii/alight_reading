package com.alight.reading

fun IntArray.println() {
    this.joinToString(", ", prefix = "[", postfix = "]")
        .also { println(it) }
}
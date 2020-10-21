package com.exd.myapplication

fun IntArray.println() {
    this.joinToString(", ", prefix = "[", postfix = "]")
        .also { println(it) }
}
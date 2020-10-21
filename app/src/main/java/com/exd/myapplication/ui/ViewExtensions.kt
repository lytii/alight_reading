package com.exd.myapplication.ui

import android.widget.ScrollView
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


/**
 * 1. Pause at top,
 * 2. slowly scroll down,
 * 3. pause at bottom,
 * 4. reset to top, repeat step 1.
 * @param interval : refresh rate to scroll
 * @param ySpeed : scrolling speed, this low and low [interval] will look smoother
 */
fun ScrollView.slowScrollDown(interval: Long = 50, ySpeed: Int = 1): Disposable {
    return Observable.interval(interval, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            when {
                // scroll down continuously
                canScrollVertically(1) -> smoothScrollBy(0, ySpeed)
            }
        }
}

fun RecyclerView.slowScrollDown(intervalMs: Long = 50, speed: Int = 1): Disposable {
    return Observable.interval(intervalMs, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            when {
                // scroll down continuously
                canScrollVertically(1) -> smoothScrollBy(0, speed)
            }
        }
}
package com.exd.myapplication.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout

class NewBehavior<V : View>(
    context: Context,
    attributes: AttributeSet
) : CoordinatorLayout.Behavior<V>(context, attributes) {

    init {
        // Extract any custom attributes out
        // preferably prefixed with behavior_ to denote they
        // belong to a behavior
    }
}


class FancyLayout(
    context: Context,
    private val attributes: AttributeSet
) : FrameLayout(context, attributes), CoordinatorLayout.AttachedBehavior {

    override fun getBehavior(): CoordinatorLayout.Behavior<*> {
        return NewBehavior<FancyLayout>(context, attributes)
    }
}

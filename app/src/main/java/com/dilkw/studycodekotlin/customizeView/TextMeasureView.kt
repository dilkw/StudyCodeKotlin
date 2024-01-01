package com.dilkw.studycodekotlin.customizeView

import android.content.Context
import android.util.AttributeSet
import android.view.View

class TextMeasureView: View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr,
        0
    ) {
        initVar()
    }

    private fun initVar() {

    }
}
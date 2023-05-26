package com.dilkw.studycodekotlin.customizeView

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class FlowLayout: ViewGroup {

    private var mMarginLeftAndRight = 50
    private var mMarginTopAndBottom = 50

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr,
        0
    ) {
        setBackgroundColor(Color.GREEN)
    }

    private var mWidth = 0
    private var mHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedWidth = 0
        var usedHeight = 0

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if (childCount > 0) {
            for (i in 0..childCount) {
                val childView = getChildAt(i) ?: continue
                val lp = childView.layoutParams

                var childWidthSpec = 0
                var childHeightSpec = 0
                when (lp.width) {
                    LayoutParams.MATCH_PARENT -> {
                        childWidthSpec =
                            if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) {
                                MeasureSpec.makeMeasureSpec(
                                    widthSize,
                                    MeasureSpec.EXACTLY
                                )
                            } else {
                                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                            }
                    }
                    LayoutParams.WRAP_CONTENT -> {
                        childWidthSpec =
                            if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) {
                                MeasureSpec.makeMeasureSpec(
                                    widthSize,
                                    MeasureSpec.AT_MOST
                                )
                            } else {
                                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                            }
                    }
                    else -> {
                        childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY)
                    }
                }
                when (lp.height) {
                    LayoutParams.MATCH_PARENT -> {
                        childHeightSpec =
                            if (heightMode == MeasureSpec.AT_MOST || heightSize == MeasureSpec.EXACTLY) {
                                MeasureSpec.makeMeasureSpec(
                                    heightSize,
                                    MeasureSpec.EXACTLY
                                )
                            } else {
                                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                            }
                    }
                    LayoutParams.WRAP_CONTENT -> {
                        childHeightSpec =
                            if (heightMode == MeasureSpec.AT_MOST || heightSize == MeasureSpec.EXACTLY) {
                                MeasureSpec.makeMeasureSpec(
                                    heightSize,
                                    MeasureSpec.AT_MOST
                                )
                            } else {
                                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                            }
                    }
                    else -> {
                        childHeightSpec =
                            MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY)
                    }

                }
                childView.measure(childWidthSpec, childHeightSpec)
                usedWidth += childView.measuredWidth
                usedHeight += childView.measuredHeight
            }
        }

        mWidth = if (widthMode == MeasureSpec.EXACTLY){
            widthSize
        } else {
            usedWidth
        }
        mHeight = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            usedHeight
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var maxLineHeight = 0
        for (i in 0..childCount) {
            val childView = getChildAt(i)
            var childLeft = 0
            var childTop = 0
            var childRight = 0
            var childBottom = 0
            var previousView: View? = null

            if (childView == null) continue

            if (i == 0) {
                childLeft += mMarginLeftAndRight
                childTop += mMarginTopAndBottom
                maxLineHeight = childTop + childView.measuredHeight
            }

            if (i > 0) {
                previousView = getChildAt(i - 1)
                if (previousView.right + childView.measuredWidth + mMarginLeftAndRight * 2 > width) {
                    childLeft += mMarginLeftAndRight
                    childTop = maxLineHeight + mMarginTopAndBottom
                } else {
                    maxLineHeight = (previousView.top + childView.measuredHeight).coerceAtLeast(maxLineHeight)
                    childLeft = previousView.right + mMarginLeftAndRight
                    childTop = previousView.top
                }
            }
            childRight = childLeft + childView.measuredWidth
            childBottom = childTop + childView.measuredHeight
            childView.layout(childLeft, childTop, childRight, childBottom)
        }
    }
}
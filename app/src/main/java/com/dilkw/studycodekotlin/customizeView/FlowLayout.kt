package com.dilkw.studycodekotlin.customizeView

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.dilkw.studycodekotlin.R

class FlowLayout : ViewGroup {

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
        setBackgroundColor(context.resources.getColor(R.color.translate_black))
    }

    private var mWidth = 0
    private var mHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedWidth = 0
        var usedHeight = 0

        var maxUsedWidth = 0
        var maxUsedHeight = 0

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if (childCount > 0) {
            for (i in 0..childCount) {
                val childView = getChildAt(i) ?: continue
                childView.setBackgroundColor(context.resources.getColor(R.color.purple_201))
                val lp = childView.layoutParams

                var childWidthSpec: Int
                var childHeightSpec: Int
                when (lp.width) {
                    LayoutParams.MATCH_PARENT -> {
                        childWidthSpec =
                            if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) {
                                MeasureSpec.makeMeasureSpec(
                                    widthSize - paddingStart - paddingEnd,
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
                                    widthSize - paddingStart + paddingEnd,
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
                                    heightSize - paddingTop - paddingBottom,
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
                                    heightSize - paddingTop - paddingBottom,
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

                if (usedWidth + childView.measuredWidth > widthSize - paddingStart + paddingEnd) {
                    maxUsedWidth = usedWidth.coerceAtLeast(maxUsedWidth)
                    usedWidth = childView.measuredWidth + paddingStart
                    usedHeight += maxUsedHeight + (if (i > 0) mMarginTopAndBottom else paddingTop + paddingBottom)
                    maxUsedHeight = childView.measuredHeight
                } else {
                    maxUsedHeight = maxUsedHeight.coerceAtLeast(childView.measuredHeight)
                    usedWidth += childView.measuredWidth + (if (i > 0) mMarginTopAndBottom else paddingStart + paddingEnd)
                }

            }
        }
        usedHeight += maxUsedHeight

        mWidth = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            maxUsedWidth
        }
        mHeight = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            usedHeight
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // 记录每一行中子View中高度最大值
        var maxLineHeight = 0
        for (i in 0..childCount) {
            val childView = getChildAt(i)
            var childLeft = 0
            var childTop = 0
            // 当前子View的前一个子View
            var previousView: View?

            if (childView == null || childView.visibility == View.GONE) continue

            fun setFirstChildViewLayout() {
                childLeft += paddingStart
                childTop += paddingTop
                maxLineHeight = childTop + childView.measuredHeight
            }

            // 当前子View为第一个时，
            // 将子View的左坐标left设置为内左部边距
            // 将子View的左坐标top设置为内顶部边距
            if (i == 0) {
                setFirstChildViewLayout()
            }

            if (i > 0) {
                previousView = getPreviousVisibleView(i - 1)
                if (previousView != null){
                    if (previousView.right + childView.measuredWidth + paddingEnd > width) {
                    childLeft += paddingStart
                    childTop = maxLineHeight + mMarginTopAndBottom
                    } else {
                        maxLineHeight =
                            (previousView.top + childView.measuredHeight).coerceAtLeast(maxLineHeight)
                        childLeft = previousView.right + mMarginLeftAndRight
                        childTop = previousView.top
                    }
                } else {
                    setFirstChildViewLayout()
                }
            }

            val childRight: Int = childLeft + childView.measuredWidth
            val childBottom: Int = childTop + childView.measuredHeight
            childView.layout(childLeft, childTop, childRight, childBottom)
        }
    }

    /**
     * 获取当前子view的上一个子view
     * @param index Int
     * @return View?
     */
    private fun getPreviousVisibleView(index: Int): View? {
        if (index < 0)
            return null
        val childView = getChildAt(index)
        return if (childView == null || childView.visibility == View.GONE) {
            getPreviousVisibleView(index - 1)
        } else {
            getChildAt(index)
        }
    }
}
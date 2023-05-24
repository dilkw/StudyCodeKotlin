package com.dilkw.studycodekotlin.customizeView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.dilkw.studycodekotlin.R


/**
 *
 */
class CircleView : View {

    private lateinit var mBackgroundPaint: Paint

    private lateinit var mForegroundPaint: Paint

    private var mWidth = 0
    private var mHeight = 0

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
        mBackgroundPaint = Paint()
        setLayerType(LAYER_TYPE_HARDWARE, mBackgroundPaint)
        mBackgroundPaint.isAntiAlias = true
        mBackgroundPaint.style = Paint.Style.STROKE
        mBackgroundPaint.strokeWidth = 80.toFloat()
        mBackgroundPaint.strokeCap = Paint.Cap.ROUND
        mBackgroundPaint.color = context.resources.getColor(R.color.purple_200)

        mForegroundPaint = Paint()
        setLayerType(LAYER_TYPE_HARDWARE, mForegroundPaint)
        mForegroundPaint.isAntiAlias = true
        mForegroundPaint.style = Paint.Style.STROKE
        mForegroundPaint.strokeWidth = 50.toFloat()
        mForegroundPaint.color = context.resources.getColor(R.color.teal_200)
        mForegroundPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        mWidth = if (widthMode == MeasureSpec.AT_MOST) {
            100
        } else {
            MeasureSpec.getSize(widthMeasureSpec)
        }

        mHeight = if (heightMode == MeasureSpec.AT_MOST) {
            100
        } else {
            MeasureSpec.getSize(heightMeasureSpec)
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas) {
        var backgroundStroke = mBackgroundPaint.strokeWidth / 2
        var foregorundStroke = mForegroundPaint.strokeWidth / 2 + (mBackgroundPaint.strokeWidth -  mForegroundPaint.strokeWidth) / 2
        canvas.drawArc(backgroundStroke, backgroundStroke, mWidth.toFloat() - backgroundStroke, mHeight.toFloat() - backgroundStroke, 0.toFloat(), 360.toFloat(), false, mBackgroundPaint)
        canvas.drawArc(foregorundStroke, foregorundStroke, mWidth.toFloat() - foregorundStroke, mHeight.toFloat() - foregorundStroke, 0.toFloat(), 90.toFloat(), false, mForegroundPaint)
    }
}
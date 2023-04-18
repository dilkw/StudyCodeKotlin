package com.dilkw.studycodekotlin.animation.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*

class RingView: View {

    private val LOG_TAG = "RingView"

    private var mPaint: Paint? = null

    private var mPaintMinute: Paint? = null

    private var mPaintHour: Paint? = null

    private var circleX = 0
    private var circleY:Int = 0

    var mSeconds = 0
        set(value){
            if (field == value) return
            field = value
            invalidate()
            addSeconds()
            Log.d(LOG_TAG, "mSeconds: $field")

        }
    private var mMinute = 0
    private var mHour = 0

    private fun addSeconds() {
        if (mSeconds == 0) {
            ++mMinute
            if (mMinute == 60) {
                ++mHour
                mMinute = 0
                if (mHour == 12) {
                    mHour = 0
                }
            }
        }
    }

    constructor(context: Context?): this(context, null)

    constructor(context: Context?, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        if (mPaint == null) {
            mPaint = Paint()
            mPaint!!.color = Color.parseColor("#E91E63")
            mPaint!!.style = Paint.Style.STROKE
            mPaint!!.strokeCap = Paint.Cap.ROUND
            mPaint!!.isAntiAlias = true
            mPaint!!.strokeWidth = 5f
            mPaint!!.textSize = 32f
            mPaint!!.textAlign = Paint.Align.CENTER
        }
        if (mPaintMinute == null) {
            mPaintMinute = Paint()
            mPaintMinute!!.color = Color.parseColor("#AA05F3")
            mPaintMinute!!.style = Paint.Style.STROKE
            mPaintMinute!!.strokeCap = Paint.Cap.ROUND
            mPaintMinute!!.isAntiAlias = true
            mPaintMinute!!.strokeWidth = 10f
        }
        if (mPaintHour == null) {
            mPaintHour = Paint()
            mPaintHour!!.color = Color.parseColor("#CC00F3")
            mPaintHour!!.style = Paint.Style.STROKE
            mPaintHour!!.strokeCap = Paint.Cap.ROUND
            mPaintHour!!.isAntiAlias = true
            mPaintHour!!.strokeWidth = 20f
        }
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
        mSeconds = calendar.get(Calendar.SECOND)
        mMinute = calendar.get(Calendar.MINUTE)
        mHour = calendar.get(Calendar.HOUR_OF_DAY) % 12
        Log.d(LOG_TAG, "    ${calendar.timeZone} : ${calendar.get(Calendar.DAY_OF_MONTH)} : ${calendar.get(Calendar.HOUR_OF_DAY)} : $mMinute : $mSeconds  ")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        circleX = MeasureSpec.getSize(widthMeasureSpec) / 2
        circleY = MeasureSpec.getSize(heightMeasureSpec) / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        @SuppressLint("DrawAllocation")
        val rectF = RectF(
            10f, 10f, width.toFloat() - 10,
            height.toFloat() - 10
        )
        //mPaint.setColor(Color.parseColor("#FF0000"));
        canvas.drawArc(rectF, -90f, 360f, false, mPaint!!)
        drawNumber(canvas)
        drawScale(canvas)
        drawPointer(canvas)
    }

    //指针刻画
    private fun drawPointer(canvas: Canvas) {
        canvas.save()
        canvas.rotate((mSeconds * 6).toFloat(), circleX.toFloat(), circleY.toFloat())
        canvas.drawLine(
            circleX.toFloat(),
            (circleY + 80).toFloat(),
            circleX.toFloat(),
            10f,
            mPaint!!
        )
        canvas.restore()
        canvas.save()
        canvas.rotate(mMinute * 6 + 0.1f * mSeconds, circleX.toFloat(), circleY.toFloat())
        //canvas.rotate(minute * 6 / 60,circleX,circleY);
        canvas.drawLine(
            circleX.toFloat(), (circleY + 70).toFloat(), circleX.toFloat(), 50f,
            mPaintMinute!!
        )
        canvas.restore()
        canvas.save()
        canvas.rotate(mHour * 30 + 0.5f * mMinute + 0.008f * mSeconds , circleX.toFloat(), circleY.toFloat())
        canvas.drawLine(
            circleX.toFloat(), (circleY + 40).toFloat(), circleX.toFloat(), 100f,
            mPaintHour!!
        )
        canvas.restore()
    }

    //刻度画笔
    private fun drawScale(canvas: Canvas) {
        canvas.save()
        val length = 10
        var isNumber: Int
        for (i in 0..59) {
            isNumber = 0
            if ((i + 1) % 5 == 0) {
                isNumber = 1
                //canvas.drawText(String.valueOf(i),getWidth()/2,60,mPaint);
            }
            canvas.rotate(6f, circleX.toFloat(), circleY.toFloat())
            canvas.drawLine(
                circleX.toFloat(), (length + isNumber * 20).toFloat() + 10, circleX.toFloat(), 10f,
                mPaint!!
            )
        }
        canvas.restore()
    }

    private fun drawNumber(canvas: Canvas) {
        canvas.drawText("12", circleX.toFloat(), 64f, mPaint!!)
        canvas.drawText("6", circleX.toFloat(), (height - 64).toFloat(), mPaint!!)
        canvas.drawText("9", 64f, (circleY + 10).toFloat(), mPaint!!)
        canvas.drawText("3", (width - 64).toFloat(), (circleY + 10).toFloat(), mPaint!!)
    }
}
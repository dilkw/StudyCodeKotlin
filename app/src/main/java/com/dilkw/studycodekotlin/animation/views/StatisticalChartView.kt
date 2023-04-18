package com.dilkw.studycodekotlin.animation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi

class StatisticalChartView: View {

    private var mPaint1: Paint? = null

    private var mPaint2: Paint? = null

    private var path: Path? = null

    private val values = floatArrayOf(10.02f, 2.02134f, 4f, 1f)

    private var progress: Float = 0f
        set(progress) {
            field = progress
            invalidate()
        }

//    fun getProgress(): Float {
//        return progress
//    }
//
//    fun setProgress(progress: Float) {
//        this.progress = progress
//        invalidate()
//    }

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int): this(context, attrs, defStyleAttr, 0)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initMPaint1()
        path = Path()
    }

    private fun initMPaint1() {
        mPaint1 = Paint()
        mPaint1!!.style = Paint.Style.FILL
        mPaint1!!.strokeWidth = 5f
        mPaint1!!.textSize = 30f
        mPaint1!!.textAlign = Paint.Align.CENTER
        mPaint1!!.color = Color.parseColor("#000000")
    }

    private fun initMPaint2() {
        mPaint2 = Paint()
        mPaint2!!.style = Paint.Style.FILL
        mPaint2!!.strokeWidth = 5f
        mPaint2!!.color = Color.parseColor("#CC00F3")
    }

    //private int columnarWidth =

    //private int columnarWidth =
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //纵坐标箭头
        path!!.moveTo(110f, 130f)
        path!!.lineTo(130f, 110f)
        path!!.lineTo(150f, 130f)
        path!!.lineTo(130f, 90f)
        //纵坐标箭头
        path!!.moveTo(780f, 680f)
        path!!.lineTo(820f, 700f)
        path!!.lineTo(780f, 720f)
        path!!.lineTo(800f, 700f)
        canvas.drawLine(130f, 100f, 130f, 700f, mPaint1!!)
        canvas.drawLine(130f, 700f, 800f, 700f, mPaint1!!)
        Log.d("imageView", "onDraw:initValue " + values.size)
        val interval = 60
        var initValue = 200
        for (value in values) {
            Log.d("imageView", "onDraw: initValue : $initValue")
            canvas.drawRect(
                initValue.toFloat(), 700 - progress * value, (initValue + interval).toFloat(), 700f,
                mPaint1!!
            )
            canvas.drawText(
                (value * progress / 50).toString(),
                (initValue + 30).toFloat(),
                700 - progress * value - 20,
                mPaint1!!
            )
            initValue += interval * 2
        }
        canvas.drawPath(path!!, mPaint1!!)
    }

}
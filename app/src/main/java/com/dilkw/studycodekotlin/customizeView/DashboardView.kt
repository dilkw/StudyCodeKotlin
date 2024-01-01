package com.dilkw.studycodekotlin.customizeView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathDashPathEffect
import android.graphics.PathEffect
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.View

/**
 * 仪表盘绘制
 */
const val OPEN_ANGLE = 120f
class DashboardView: View {

    private val path = Path()
    private val dashPath = Path()
    private val paint = Paint()
    lateinit var pathEffect: PathDashPathEffect

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr,
        0
    )

    init {

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        path.reset()
        path.addArc(width / 2f - 150f, height / 2f - 150f, width / 2f + 150f, height / 2f + 150f,90 + OPEN_ANGLE / 2f, 360f - OPEN_ANGLE)
        dashPath.addRect(0f, 0f, 5f, 20f, Path.Direction.CCW)
        val pathMeasure = PathMeasure(path, false)
        pathEffect = PathDashPathEffect(dashPath, pathMeasure.length / 20, 0f, PathDashPathEffect.Style.ROTATE)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, paint)
        paint.pathEffect = pathEffect
        canvas.drawArc(width / 2f - 150f, height / 2f - 150f, width / 2f + 150f, height / 2f + 150f,90 + OPEN_ANGLE / 2f, 360f - OPEN_ANGLE, false, paint)
        paint.pathEffect = null
    }

}
package com.example.taskforeffectivemobile

import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomView(
    private val context: Context,
    private val attr: AttributeSet?,
    private val defStyleAttr: Int,
    private val defStyleRes: Int,
) : View(context, attr, defStyleAttr, defStyleRes) {


    private var clickCount = 0
    private val startWidth = 20f
    private var squareColorSize: Float = startWidth
    private val endWidth = 350f
    private val startHeight = 20f
    private val endHeight = 200f

    constructor(context: Context) : this(context, null, 0, 0)

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0, 0)

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attr,
        defStyleAttr,
        0
    )


    private val squarePainter: Paint = Paint()
        .apply {
            color = Color.BLACK
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }

    private val fillPainter: Paint = Paint()
        .apply {
            color = Color.BLACK
            style = Paint.Style.FILL_AND_STROKE
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (squareColorSize > startWidth) {
            canvas.drawRect(startWidth, startHeight, squareColorSize, endHeight, fillPainter)
        }

        canvas.drawRect(startWidth, startHeight, endWidth, endHeight, squarePainter)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {

        if(event.action == MotionEvent.ACTION_DOWN){
            val x = event.x
            val y = event.y


            if(x in startWidth..endWidth && y in startHeight..endHeight){
                changeSquareColorSize()
                println("work")
                return true
            }

            return true
        }

        return super.onTouchEvent(event)
    }

    private fun changeSquareColorSize() {
        if(clickCount < 10){
            clickCount++
            println("Click count: $clickCount")
            val totalWidth = endWidth - startWidth
            val step = totalWidth/10
            squareColorSize = startWidth + (step*clickCount)
        } else {
            squareColorSize = startWidth
            clickCount = 0

        }

        invalidate()

    }
}
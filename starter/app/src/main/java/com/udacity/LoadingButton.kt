package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator.ofFloat()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    private var backgroud: Rect = Rect(0, 0, 0, 0)
    private val paint = Paint().apply {
        color = Color.GREEN
    }
    private val textPaint = Paint().apply {
        color = Color.BLACK
        bold
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }

    init {
        isClickable = true
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.RED)
        canvas?.drawRect(backgroud, paint)
        val xPos = (width / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
        val yPos = (height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
        canvas?.drawText(context.getString(R.string.download_btn_label), xPos, yPos,  textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {


    }

    override fun performClick(): Boolean {
        runAnimations()
        //  Redraw the view
        invalidate()
        return true
    }

    private fun runAnimations() {
        if (!valueAnimator.isStarted) {
            valueAnimator = ValueAnimator.ofInt(0, width).apply {
                duration = 2000
                addUpdateListener {
                    backgroud = Rect(0, 0, it.animatedValue as Int, height)
                    invalidate()
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        backgroud = Rect(0, 0, 0, 0)
                        invalidate()
                    }
                })
            }
            valueAnimator.start()
        }
    }
}
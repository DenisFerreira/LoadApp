package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var loadingDuration: Float = 0.0f
    private var loadingColor: Int = 0
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator.ofFloat()
    private  var buttonLabel = context.getString(R.string.download_btn_label)
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, old, new ->
        when(new) {
            ButtonState.Clicked -> runAnimations()
            ButtonState.Loading -> buttonLabel = context.getString(R.string.loading_btn_label)
            ButtonState.Completed -> buttonLabel = context.getString(R.string.download_btn_label)
        }
    }

    private var background: Rect = Rect(0, 0, 0, 0)
    private var sweepAngle: Float = 0f
    private val backgroundPaint = Paint().apply {
        color = resources.getColor(R.color.colorPrimaryDark, context.theme)
    }
    private val textPaint = Paint().apply {
        color = resources.getColor(R.color.white, context.theme)
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }
    private lateinit var arcPaint: Paint
    private var xPos = 0f
    private var yPos = 0f
    private var xCPos = 0f
    private var yCPos = 0f
    init {
        isClickable = true
        buttonState = ButtonState.Completed
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, resources.getColor(R.color.colorAccent, context.theme))
            loadingDuration = getFloat(R.styleable.LoadingButton_loadingDuration, 2000f)
            arcPaint = Paint().apply {
            color = loadingColor
        }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        xPos = (w / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
        yPos = (h / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
        xCPos = xPos + (w / 4f)
        yCPos = (h / 4f)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(resources.getColor(R.color.colorPrimary, context.theme))
        canvas?.drawRect(background, backgroundPaint)
        canvas?.drawText(buttonLabel, xPos, yPos, textPaint)
        canvas?.drawArc(xCPos, yCPos, xCPos+100f, yCPos+100f, 0f, sweepAngle, true, arcPaint)
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

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.Clicked
        return true
    }

    private fun runAnimations() {
        if (!valueAnimator.isStarted) {
            valueAnimator = ValueAnimator.ofInt(0, width).apply {
                duration = loadingDuration.toLong()
                addUpdateListener {
                    background = Rect(0, 0, it.animatedValue as Int, height)
                    sweepAngle = (it.animatedValue as Int).toFloat() * 360f / width.toFloat()
                    invalidate()
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        background = Rect(0, 0, 0, 0)
                        sweepAngle = 0f
                        invalidate()
                        buttonState = ButtonState.Completed
                    }
                })
            }
            valueAnimator.start()
            buttonState = ButtonState.Loading
        }
    }
}
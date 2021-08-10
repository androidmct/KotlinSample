package com.bytepace.dimusco.ui.components.colorpicker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class HuePicker : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var selectedColor: Int
        get() = bitmap.getPixel(markerPos.toInt(), 0)
        set(value) {
            var hue = FloatArray(3).also { Color.colorToHSV(value, it) }[0]
            markerPos = pickerWidth / 360 * hue + left + 1
            if (markerPos > right) {
                markerPos = right
            }
            invalidate()
        }

    private val markerRadius: Float
        get() = height.toFloat() / 2 - markerBorderWidth / 2
    private val markerBorderedRadius: Float
        get() = markerRadius + markerBorderWidth / 2
    private val left: Float
        get() = markerBorderedRadius
    private val right: Float
        get() = width.toFloat() - markerBorderedRadius
    private val pickerWidth: Float
        get() = width.toFloat() - markerBorderedRadius * 2
    private val markerBorderWidth = 5f

    private val markerStroke = Paint().apply {
        style = Paint.Style.STROKE
        flags = Paint.ANTI_ALIAS_FLAG
        strokeWidth = markerBorderWidth - 2
        color = Color.WHITE
    }
    private val markerStrokeOutline = Paint().apply {
        style = Paint.Style.STROKE
        flags = Paint.ANTI_ALIAS_FLAG
        strokeWidth = markerBorderWidth
        color = Color.DKGRAY
    }
    private val markerFill = Paint()
    private val bitmapPaint = Paint(Paint.DITHER_FLAG)
    private val hueColors = intArrayOf(
        Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED
    )

    private var markerPos = 0f
    private var callback: ((Int) -> Unit)? = null

    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas

    fun setOnHueChangedCallback(callback: (Int) -> Unit) {
        this.callback = callback
    }

    private fun drawMarker(canvas: Canvas) {
        canvas.apply {
            drawCircle(
                markerPos,
                (height / 2).toFloat(),
                markerRadius,
                markerFill.apply { this.color = selectedColor }
            )
            drawCircle(markerPos, (height / 2).toFloat(), markerRadius, markerStrokeOutline)
            drawCircle(markerPos, (height / 2).toFloat(), markerRadius, markerStroke)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val huePaint = Paint().apply {
            shader = LinearGradient(left, 0f, right, 0f, hueColors, null, Shader.TileMode.CLAMP)
        }
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap).apply { drawRect(left, 0f, right, h.toFloat(), huePaint) }
        markerPos = w.toFloat() / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            drawBitmap(bitmap, 0f, 0f, bitmapPaint)
            drawMarker(this)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
            markerPos = when {
                event.x < left -> left + 1
                event.x > right -> right - 1
                else -> event.x
            }
            callback?.invoke(selectedColor)
        }
        invalidate()
        return true
    }
}
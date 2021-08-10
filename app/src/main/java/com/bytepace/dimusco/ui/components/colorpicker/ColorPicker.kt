package com.bytepace.dimusco.ui.components.colorpicker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ColorPicker : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var selectedColor: Int
        get() = bitmap.getPixel(markerX.toInt(), markerY.toInt())
        set(value) {
            val hsv = FloatArray(3).also { Color.colorToHSV(value, it) }
            moveMarker(
                pickerWidth * hsv[1] + left + 1,
                pickerHeight * (1 - hsv[2]) + top + 1
            )
            hue = value
        }

    var hue: Int = Color.RED
        set(value) {
            field = Color.HSVToColor(
                FloatArray(3).also {
                    Color.colorToHSV(value, it)
                    it[1] = 1f
                    it[2] = 1f
                })
            updateBackground()
            invalidate()
            onColorChangedCallback?.invoke(selectedColor)
        }

    private val markerRadius = 29f
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

    private val markerBorderedRadius: Float
        get() = markerRadius + markerBorderWidth / 2
    private val left: Float
        get() = markerBorderedRadius
    private val top: Float
        get() = markerBorderedRadius
    private val right: Float
        get() = width.toFloat() - markerBorderedRadius
    private val bottom: Float
        get() = height.toFloat() - markerBorderedRadius
    private val pickerWidth: Float
        get() = width.toFloat() - markerBorderedRadius * 2
    private val pickerHeight: Float
        get() = height.toFloat() - markerBorderedRadius * 2

    private var markerX = 0f
    private var markerY = 0f
    private var onColorChangedCallback: ((Int) -> Unit)? = null

    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var huePaint: Paint
    private lateinit var overlayBrightness: Paint
    private lateinit var overlaySaturation: Paint

    fun setOnColorChangedCallback(callback: (Int) -> Unit) {
        onColorChangedCallback = callback
    }

    private fun updateBackground() {
        canvas.apply {
            drawRect(left, top, right, bottom, huePaint.apply { color = hue })
            drawRect(left, top, right, bottom, overlaySaturation)
            drawRect(left, top, right, bottom, overlayBrightness)
        }
    }

    private fun drawMarker(canvas: Canvas) {
        canvas.apply {
            drawCircle(
                markerX, markerY, markerRadius, markerFill.apply { this.color = selectedColor }
            )
            drawCircle(markerX, markerY, markerRadius, markerStrokeOutline)
            drawCircle(markerX, markerY, markerRadius, markerStroke)
        }
    }

    private fun moveMarker(x: Float, y: Float) {
        markerX = when {
            x < left -> left + 1
            x > right -> right - 1
            else -> x
        }
        markerY = when {
            y < top -> top + 1
            y > bottom -> bottom - 1
            else -> y
        }
        onColorChangedCallback?.invoke(selectedColor)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        markerX = w.toFloat() / 2
        markerY = h.toFloat() / 2
        huePaint = Paint(hue)
        overlayBrightness = Paint().apply {
            shader = LinearGradient(
                left, top, left, bottom, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP
            )
        }
        overlaySaturation = Paint().apply {
            shader = LinearGradient(
                left, top, right, top, Color.WHITE, Color.TRANSPARENT, Shader.TileMode.CLAMP
            )
        }
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        updateBackground()
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
            moveMarker(event.x, event.y)
        }
        invalidate()
        return true
    }
}
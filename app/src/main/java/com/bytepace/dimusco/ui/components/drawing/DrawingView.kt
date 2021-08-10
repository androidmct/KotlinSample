package com.bytepace.dimusco.ui.components.drawing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Color
import android.os.Parcelable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bytepace.dimusco.data.model.*
import com.bytepace.dimusco.data.model.Picture
//import com.bytepace.dimusco.ui.editor.EditorViewModel
import com.bytepace.dimusco.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs
import kotlin.math.max

private const val TOUCH_TOLERANCE = 1f

private const val BITMAP_FILEPATH = "$EDITOR_TEMP_FOLDER/temp"

private const val DEFAULT_TEXT = "Text"

class DrawingView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

//    var mode: EditorViewModel.EditMode? = null
//        set(value) {
//            field = value
//            if (value != null) {
//                onPictureDeselect()
//                isTyping = false
//                hideKeyboard()
//                selectedPictureIndex = null
//            }
//            invalidate()
//        }
    var callback: DrawingViewCallback? = null
    var layer: Layer? = null
    var pictures: MutableList<Picture> = mutableListOf()
        set(value) {
            field = value
            selectedPictureIndex = null
            getImagePictureBitmap(value)?.let {
                bitmap = it.copy(it.config, true)
                canvas = Canvas(bitmap)
            }
            invalidate()
        }
    var color: Int = 0
        set(value) {
            field = value
            setColor()
            updateSelectedPictureColor()
        }
    var opacity: Float = 1f
        set(value) {
            field = value
            setColor()
            updateSelectedPictureTransparency()
        }
    var brushScale: Float = 1f
        set(value) {
            field = value
            brushPaint.strokeWidth = value * baseStrokeWidth
            updateSelectedPictureLineThickness()
        }
    var eraserScale: Float = 1f
        set(value) {
            field = value
            eraserPaint.strokeWidth = value * baseStrokeWidth
        }
    var symbol: Symbol? = null
    var fontSize: Float = 0.5f

    private val bitmapPaint = Paint(Paint.DITHER_FLAG)
    private val brushPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            isDither = true
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = baseStrokeWidth
        }
    }
    private val eraserPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = baseStrokeWidth
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }
    private val symbolPaint by lazy {
        Paint().apply {
            colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
            isAntiAlias = true
            isDither = true
            isFilterBitmap = true
        }
    }
    private val selectionPaint by lazy {
        Paint().apply {
            color = Color.BLACK
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
    }
    private val eraserSelectionPaint by lazy {
        Paint().apply {
            color = Color.RED
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
    }

    private var selectedPictureIndex: Int? = null
    private var path = Path()
    private var pathPoints = arrayListOf<PathPoint>()
    private var cX: Float = 0f
    private var cY: Float = 0f
    private var touchOffsetX = 0f
    private var touchOffsetY = 0f
    private var baseStrokeWidth: Float = BASE_BRUSH_SIZE
        set(value) {
            field = value
            brushPaint.strokeWidth = value * brushScale
            eraserPaint.strokeWidth = value * eraserScale
        }
    private var isObjectMoved = false
    private var isTyping = false
    private var multiplier: Float = 1f
        private set(value) {
            field = value
            baseStrokeWidth = value * BASE_BRUSH_SIZE
        }

    private lateinit var canvas: Canvas
    lateinit var bitmap: Bitmap private set

    fun getSelectedPictureAsEditorAction(): EditorAction? {
        var result: EditorAction? = null

        selectedPictureIndex?.let {
            val picture = pictures[it]
            if (!isEditorActionIgnored(picture)) {
                result = EditorAction(EditorAction.Type.EDIT, pictures[it].clone(), it)
            }
        }
        return result
    }

    fun deleteSelectedPicture() {
        selectedPictureIndex?.let {
            if (!pictures.indices.contains(it)) return
            val picture = pictures.removeAt(it)
            selectedPictureIndex = null
            if (!isEditorActionIgnored(picture)) {
                callback?.onEditorAction(EditorAction(EditorAction.Type.REMOVE, picture, it))
            }
            callback?.onPictureDeselected()
            hideKeyboard()
            invalidate()
        }
    }

    fun scaleSelectedPicture(scale: Float) {
        selectedPictureIndex?.let {
            pictures[it].apply {
                val newWidth = width * scale
                val newHeight = height * scale
                if (newWidth * multiplier > this@DrawingView.width || newHeight * multiplier > this@DrawingView.height) {
                    return
                }

                width = newWidth
                height = newHeight
                points.forEach { point ->
                    point.x = x + (point.x - x) * scale
                    point.y = y + (point.y - y) * scale
                }
                if (type == PICTURE_TYPE_TEXT) {
                    fontSize *= scale
                }
                invalidate()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        multiplier = max(w, h) / LAYER_SIZE_MAX // TODO Делить на максимальный размер слоя

        bitmap = if (!::bitmap.isInitialized) {
            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createScaledBitmap(bitmap, w, h, true)
        }
        canvas = Canvas(bitmap)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        callback?.onRequiredImage(BITMAP_FILEPATH)?.let { bitmap ->
            post {
                this@DrawingView.bitmap = createScaledBitmap(bitmap)
                invalidate()
            }
        }
        super.onRestoreInstanceState(state)
    }

    // region RENDERING
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawBitmap(bitmap, 0f, 0f, bitmapPaint)
//        when (mode) {
//            EditorViewModel.EditMode.ERASER -> canvas?.drawPath(path, eraserPaint)
//            else -> canvas?.drawPath(path, brushPaint)
//        }
        drawPictures(canvas)
    }

    private fun drawPictures(canvas: Canvas?) {
        pictures.forEachIndexed { index, picture ->
            when (picture.type) {
                PICTURE_TYPE_BRUSH -> drawBrushPicture(canvas, picture)
                PICTURE_TYPE_SYMBOL -> drawSymbolPicture(canvas, picture)
                PICTURE_TYPE_TEXT -> drawTextPicture(canvas, picture)
            }
//            if (mode == EditorViewModel.EditMode.ERASER && picture.type != PICTURE_TYPE_IMAGE) {
//                drawEraserSelection(canvas, picture)
//            } else if (selectedPictureIndex == index) {
//                drawSelection(canvas, picture)
//            }
        }
    }

    private fun drawBrushPicture(canvas: Canvas?, picture: Picture) {
        val paint = Paint(brushPaint).apply {
            color = getColorWithOpacity(picture.color, picture.transparency)
            strokeWidth = picture.lineThickness * baseStrokeWidth
        }
        val path = Path()
        var cX = picture.points[0].x * multiplier
        var cY = picture.points[0].y * multiplier
        path.moveTo(cX, cY)
        picture.points.forEach {
            path.quadTo(cX, cY, (it.x * multiplier + cX) / 2, (it.y * multiplier + cY) / 2)
            cX = it.x * multiplier
            cY = it.y * multiplier
        }
        path.lineTo(cX, cY)
        canvas?.drawPath(path, paint)
    }

    private fun drawSymbolPicture(canvas: Canvas?, picture: Picture) {
        getSymbolImage(picture.symbol)?.let {
            val paint = Paint(symbolPaint).apply {
                colorFilter = PorterDuffColorFilter(
                    getColorWithOpacity(picture.color, picture.transparency),
                    PorterDuff.Mode.SRC_IN
                )
            }
            val rect = Rect(
                (picture.x * multiplier).toInt(),
                (picture.y * multiplier).toInt(),
                ((picture.x + picture.width) * multiplier).toInt(),
                ((picture.y + picture.height) * multiplier).toInt()
            )
            canvas?.drawBitmap(it, null, rect, paint)
        }
    }

    @Suppress("CHANGING_ARGUMENTS_EXECUTION_ORDER_FOR_NAMED_VARARGS")
    private fun drawTextPicture(canvas: Canvas?, picture: Picture) {
        val paint = Paint().apply {
            textSize = picture.fontSize * multiplier
            color = getColorWithOpacity(picture.color, picture.transparency)
            isAntiAlias = true
            isDither = true
        }
//        val lines = picture.text.split(delimiters = arrayOf("\n"), ignoreCase = false, limit = 0)
        val lines = picture.text.split("\n")
        for (i in lines.indices) {
            canvas?.drawText(
                lines[i],
                picture.x * multiplier,
                (picture.y + picture.fontSize + picture.fontSize * i) * multiplier,
                paint
            )
        }
    }

    private fun drawSelection(canvas: Canvas?, picture: Picture) {
        drawSelectionRect(canvas, picture, selectionPaint)
    }

    private fun drawEraserSelection(canvas: Canvas?, picture: Picture) {
        drawSelectionRect(canvas, picture, eraserSelectionPaint)
    }

    private fun drawSelectionRect(canvas: Canvas?, picture: Picture, paint: Paint) {
        canvas?.drawRect(
            picture.x * multiplier,
            picture.y * multiplier,
            (picture.x + picture.width) * multiplier,
            (picture.y + picture.height) * multiplier,
            paint
        )
    }
    // endregion

    // region TOUCH HANDLING
    // region TOUCH HANDLING - GENERAL
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchStart(event.x, event.y)
            MotionEvent.ACTION_MOVE -> onTouchMove(event.x, event.y)
            MotionEvent.ACTION_UP -> onTouchEnd()
        }
        invalidate()
        return true
    }

    private fun onTouchStart(x: Float, y: Float) {
//        when (mode) {
//            EditorViewModel.EditMode.DRAW -> onTouchStartDraw(x, y)
//            EditorViewModel.EditMode.ERASER -> onTouchStartEraser(x, y)
//            EditorViewModel.EditMode.SYMBOL -> onTouchStartSymbol(x, y)
//            EditorViewModel.EditMode.TEXT -> onTouchStartText(x, y)
//            null -> onTouchStartObject(x, y)
//        }
        cX = x
        cY = y
    }

    private fun onTouchMove(x: Float, y: Float) {
        if (abs(x - cX) >= TOUCH_TOLERANCE || abs(y - cY) >= TOUCH_TOLERANCE) {
//            when (mode) {
//                EditorViewModel.EditMode.DRAW -> onTouchMoveDraw(x, y)
//                EditorViewModel.EditMode.ERASER -> onTouchMoveEraser(x, y)
//                else -> onTouchMoveObject(x, y)
//            }
        }
    }

    private fun onTouchEnd() {
//        when (mode) {
//            EditorViewModel.EditMode.DRAW -> onTouchEndDraw()
//            EditorViewModel.EditMode.ERASER -> onTouchEndEraser()
//            else -> onTouchEndObject()
//        }
    }
    // endregion

    // region TOUCH HANDLING - DRAWING
    private fun onTouchStartDraw(x: Float, y: Float) {
        path.reset()
        path.moveTo(x, y)

//        if (mode == EditorViewModel.EditMode.DRAW && layer?.isImageDrawings != true) {
//            pathPoints.add(PathPoint(x / multiplier, y / multiplier))
//        } else {
//            createImageBackupAsync()
//        }
    }

    private fun onTouchMoveDraw(x: Float, y: Float) {
        path.quadTo(cX, cY, (x + cX) / 2, (y + cY) / 2)
        cX = x
        cY = y

//        if (mode == EditorViewModel.EditMode.DRAW && layer?.isImageDrawings != true) {
//            pathPoints.add(PathPoint(x / multiplier, y / multiplier))
//        }
    }

    private fun onTouchEndDraw() {
        path.lineTo(cX, cY)
        if (layer?.isImageDrawings == true) {
            onTouchEndDrawImage()
        } else {
            onTouchEndDrawObject()
        }
        path.reset()
    }

    private fun onTouchEndDrawImage() {
        canvas.drawPath(path, brushPaint)
        GlobalScope.launch { createImageBackup(BITMAP_FILEPATH) }
    }

    private fun onTouchEndDrawObject() {
        createBrushPicture()
        pathPoints.clear()
        onPictureCreated()
    }
    // endregion

    // region TOUCH HANDLING - OBJECTS
    private fun onTouchStartObject(x: Float, y: Float) {
        val touchedPicture = getTouchedPicture(x, y)
        if (selectedPictureIndex != null && selectedPictureIndex != touchedPicture) {
            onPictureDeselect()
        }
        selectedPictureIndex = touchedPicture
        selectedPictureIndex?.let {
            touchOffsetX = x / multiplier - pictures[it].x
            touchOffsetY = y / multiplier - pictures[it].y
            isObjectMoved = false
            callback?.onPictureSelected(pictures[it])
        }
    }

    private fun onTouchStartSymbol(x: Float, y: Float) {
        callback?.onPictureDeselected()
        createSymbolPicture(x, y)
        onPictureCreated()
    }

    private fun onTouchStartText(x: Float, y: Float) {
        callback?.onPictureDeselected()
        createTextPicture(x, y)
        onPictureCreated()
    }

    private fun onTouchMoveObject(x: Float, y: Float) {
        selectedPictureIndex?.let {
            val picture = pictures[it]

            if (!isObjectMoved && !isEditorActionIgnored(picture)) {
                callback?.onEditorAction(
                    EditorAction(EditorAction.Type.EDIT, picture.clone(), it)
                )
            }
            when (picture.type) {
                PICTURE_TYPE_BRUSH -> moveBrushPicture(picture, x, y)
                else -> moveObjectPicture(picture, x, y)
            }
            isObjectMoved = true
        }
    }

    private fun onTouchEndObject() {
        selectedPictureIndex?.let {
            pictures[it].let { picture ->
                isTyping = picture.type == PICTURE_TYPE_TEXT && !isObjectMoved
            }
        }
        if (selectedPictureIndex == null) {
            isTyping = false
            callback?.onPictureDeselected()
        }
        toggleKeyboard()
    }

    private fun getTouchedPicture(x: Float, y: Float): Int? {
        for (index in pictures.size - 1 downTo 0) {
            val picture = pictures[index]
            if (picture.type != PICTURE_TYPE_IMAGE) {
                if (picture.x * multiplier < x && (picture.x + picture.width) * multiplier > x
                    && picture.y * multiplier < y && (picture.y + picture.height) * multiplier > y
                ) {
                    return index
                }
            }
        }
        return null
    }

    private fun moveObjectPicture(picture: Picture, x: Float, y: Float) {
        picture.x = x / multiplier - touchOffsetX
        picture.y = y / multiplier - touchOffsetY
    }

    private fun moveBrushPicture(picture: Picture, x: Float, y: Float) {
        val dX = x / multiplier - touchOffsetX - picture.x
        val dY = y / multiplier - touchOffsetY - picture.y
        picture.points.forEach {
            it.x += dX
            it.y += dY
        }
        picture.x += dX
        picture.y += dY
    }
    // endregion

    // region TOUCH HANDLING - ERASER
    private fun onTouchStartEraser(x: Float, y: Float) {
        selectedPictureIndex = getTouchedPicture(x, y)
        if (selectedPictureIndex != null) {
            selectedPictureIndex!!.let {
                touchOffsetX = x / multiplier - pictures[it].x
                touchOffsetY = y / multiplier - pictures[it].y
            }
            isObjectMoved = false
        } else {
            onTouchStartDraw(x, y)
        }
    }

    private fun onTouchMoveEraser(x: Float, y: Float) {
        when {
            selectedPictureIndex != null -> onTouchMoveObject(x, y)
            else -> onTouchMoveDraw(x, y)
        }
    }

    private fun onTouchEndEraser() {
        if (selectedPictureIndex == null) {
            canvas.drawPath(path, eraserPaint)
            GlobalScope.launch { createImageBackup(BITMAP_FILEPATH) }
        } else if (!isObjectMoved) {
            deleteSelectedPicture()
        } else {
            selectedPictureIndex = null
        }
        path.reset()
    }
    // endregion
    // endregion

    private fun createSymbolPicture(x: Float, y: Float) {
        symbol?.let { symbol ->
            val bitmap = getSymbolImage(symbol.value) ?: return
            val picture = Picture(
                x = x / multiplier,
                y = y / multiplier,
                width = bitmap.width * symbol.scale,
                height = bitmap.height * symbol.scale,
                type = PICTURE_TYPE_SYMBOL,
                color = color,
                order = pictures.maxByOrNull { it.order }?.order?.plus(1) ?: 0,
                transparency = opacity,
                symbol = symbol.value,
                updatedAt = null
            )
            selectedPictureIndex = createPicture(picture)
        }
    }

    private fun createTextPicture(x: Float, y: Float) {
        val fontSize = 6 + (42 / fontSize)
        val bounds = getTextBounds(DEFAULT_TEXT, fontSize)
        val picture = Picture(
            x = x / multiplier,
            y = y / multiplier,
            width = bounds.width().toFloat(),
            height = bounds.height().toFloat() + fontSize / 3,
            type = PICTURE_TYPE_TEXT,
            color = color,
            fontSize = fontSize,
            order = pictures.maxByOrNull { it.order }?.order?.plus(1) ?: 0,
            transparency = opacity,
            text = DEFAULT_TEXT,
            updatedAt = null
        )
        selectedPictureIndex = createPicture(picture)
    }

    private fun createBrushPicture() {
        val bounds = getBrushPictureBounds()
        val picture = Picture(
            x = bounds[0],
            y = bounds[1],
            width = bounds[2] - bounds[0],
            height = bounds[3] - bounds[1],
            type = PICTURE_TYPE_BRUSH,
            color = color,
            order = pictures.maxByOrNull { it.order }?.order?.plus(1) ?: 0,
            transparency = opacity,
            lineThickness = brushScale,
            points = pathPoints.toList(),
            updatedAt = null
        )
        selectedPictureIndex = createPicture(picture)
    }

    private fun createPicture(picture: Picture): Int {
        val index = pictures.size
        pictures.add(index, picture)
        if (!isEditorActionIgnored(picture)) {
            callback?.onEditorAction(EditorAction(EditorAction.Type.ADD, index = index))
        }
        return index
    }

    private fun onPictureCreated() {
        selectedPictureIndex?.let {
            touchOffsetX = 0f
            touchOffsetY = 0f
            isObjectMoved = false
            callback?.onPictureSelected(pictures[it])
        }
    }

    private fun onPictureDeselect() {
        selectedPictureIndex?.let {
            if (layer?.isImageSymbols == true && pictures[it].type == PICTURE_TYPE_SYMBOL) {
                createImageBackupAsync(bitmap.copy(bitmap.config, false))
                return drawSymbolImage(pictures.removeAt(it))
            }
            if (layer?.isImageText == true && pictures[it].type == PICTURE_TYPE_TEXT) {
                createImageBackupAsync(bitmap.copy(bitmap.config, false))
                return drawTextImage(pictures.removeAt(it))
            }
        }
    }

    private fun getBrushPictureBounds(): FloatArray {
        var left = IMAGE_SIZE_MAX.toFloat()
        var top = IMAGE_SIZE_MAX.toFloat()
        var right = -1f
        var bottom = -1f
        pathPoints.forEach {
            if (it.x < left) left = it.x
            if (it.x > right) right = it.x
            if (it.y < top) top = it.y
            if (it.y > bottom) bottom = it.y
        }
        left -= brushPaint.strokeWidth / 2 / multiplier
        top -= brushPaint.strokeWidth / 2 / multiplier
        right += brushPaint.strokeWidth / 2 / multiplier
        bottom += brushPaint.strokeWidth / 2 / multiplier

        return floatArrayOf(left, top, right, bottom)
    }

    private fun getBrushPictureBounds(picture: Picture): FloatArray {
        var left = IMAGE_SIZE_MAX.toFloat()
        var top = IMAGE_SIZE_MAX.toFloat()
        var right = -1f
        var bottom = -1f
        picture.points.forEach {
            if (it.x < left) left = it.x
            if (it.x > right) right = it.x
            if (it.y < top) top = it.y
            if (it.y > bottom) bottom = it.y
        }
        left -= brushPaint.strokeWidth / 2 / multiplier
        top -= brushPaint.strokeWidth / 2 / multiplier
        right += brushPaint.strokeWidth / 2 / multiplier
        bottom += brushPaint.strokeWidth / 2 / multiplier

        return floatArrayOf(left, top, right, bottom)
    }

    private fun drawSymbolImage(picture: Picture) {
        getSymbolImage(picture.symbol)?.let {
            val rect = Rect(
                (picture.x * multiplier).toInt(),
                (picture.y * multiplier).toInt(),
                ((picture.x + picture.width) * multiplier).toInt(),
                ((picture.y + picture.height) * multiplier).toInt()
            )
            canvas.drawBitmap(it, null, rect, symbolPaint)
        }
    }

    private fun drawTextImage(picture: Picture) {
        val paint = Paint().apply {
            textSize = picture.fontSize * multiplier
            color = getColorWithOpacity(picture.color, picture.transparency)
            isAntiAlias = true
            isDither = true
        }
        canvas.drawText(
            picture.text,
            picture.x * multiplier,
            (picture.y + picture.fontSize) * multiplier,
            paint
        )
    }

    private fun getTextBounds(picture: Picture): Rect {
        return getTextBounds(picture.text, picture.fontSize)
    }

    @Suppress("CHANGING_ARGUMENTS_EXECUTION_ORDER_FOR_NAMED_VARARGS")
    private fun getTextBounds(text: String, fontSize: Float): Rect {
        val paint = Paint().apply { textSize = fontSize }
        var width = 0

//        val lines = text.split(delimiters = arrayOf("\n"), ignoreCase = false, limit = 0)
        val lines = text.split("\n")
        lines.forEach {
            val bounds = Rect().apply { paint.getTextBounds(it, 0, it.length, this) }
            if (bounds.right > width) {
                width = bounds.right
            }
        }
        return Rect(0, 0, width, (fontSize * lines.size).toInt())
    }

    private fun resizeTextPicture(picture: Picture) {
        val bounds = getTextBounds(picture)
        picture.apply {
            width = bounds.width().toFloat()
            height = bounds.height().toFloat() + fontSize / 3
        }
    }

    private fun getImagePictureBitmap(pictures: List<Picture>): Bitmap? {
        pictures.find { it.type == PICTURE_TYPE_IMAGE }?.let {
            callback?.onRequiredImage(it.filePath)?.let { bitmap ->
                return createScaledBitmap(bitmap)
            }
        }
        return null
    }

    private fun createScaledBitmap(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun setColor() {
        brushPaint.color = getColorWithOpacity(color)
        symbolPaint.colorFilter = PorterDuffColorFilter(
            getColorWithOpacity(color),
            PorterDuff.Mode.SRC_IN
        )
    }

    private fun updateSelectedPictureColor() {
        selectedPictureIndex?.let {
            val picture = pictures[it]
            val color = getColorWithOpacity(this.color)

            if (color != picture.color) {
                if (!isEditorActionIgnored(picture)) {
                    callback?.onEditorAction(
                        EditorAction(EditorAction.Type.EDIT, picture.clone(), it)
                    )
                }
                picture.color = color
                invalidate()
            }
        }
    }

    private fun updateSelectedPictureTransparency() {
        selectedPictureIndex?.let {
            pictures[it].transparency = opacity
            invalidate()
        }
    }

    private fun updateSelectedPictureLineThickness() {
        selectedPictureIndex?.let {
            pictures[it].apply {
                if (type == PICTURE_TYPE_BRUSH) {
                    lineThickness = brushScale
                    getBrushPictureBounds(this).also { bounds ->
                        x = bounds[0]
                        y = bounds[1]
                        width = bounds[2] - bounds[0]
                        height = bounds[3] - bounds[1]
                    }
                    invalidate()
                }
            }
        }
    }

    private fun createImageBackupAsync(bitmap: Bitmap? = null) {
        GlobalScope.launch {
            createImageBackup(forcedBitmap = bitmap)?.let {
                callback?.onEditorAction(
                    EditorAction(EditorAction.Type.IMAGE, filepath = it)
                )
            }
        }
    }

    private fun createImageBackup(filepath: String? = null, forcedBitmap: Bitmap? = null): String? {
        val path = filepath ?: "Editor/${UUID.randomUUID()}"
        val bitmap = forcedBitmap ?: this.bitmap
        when (val throwable = saveImage(path, bitmap.copy(bitmap.config, false))) {
            null -> return path
            else -> throwable.printStackTrace()
        }
        return null
    }

    private fun getColorWithOpacity(color: Int): Int {
        return getColorWithOpacity(color, opacity)
    }

    private fun isEditorActionIgnored(picture: Picture): Boolean {
        return picture.type == PICTURE_TYPE_SYMBOL && layer?.isImageSymbols == true ||
                picture.type == PICTURE_TYPE_TEXT && layer?.isImageText == true
    }

    private fun toggleKeyboard() {
        when (isTyping) {
            true -> showKeyboard()
            false -> hideKeyboard()
        }
    }

    private fun showKeyboard() {
        requestFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }

    private fun hideKeyboard() {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(windowToken, 0)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        selectedPictureIndex?.let {
            val picture = pictures[it]

            if (isTyping) {
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    if (!isEditorActionIgnored(picture)) {
                        callback?.onEditorAction(
                            EditorAction(EditorAction.Type.EDIT, picture.clone(), it)
                        )
                    }
                    val key = event.unicodeChar
                    if (key == 0) {
                        if (event.keyCode == KeyEvent.KEYCODE_DEL) {
                            picture.text.apply {
                                if (length > 0) {
                                    picture.text = substring(0, length - 1)
                                }
                            }
                        }
                    } else {
                        picture.text += key.toChar()
                    }
                    resizeTextPicture(picture)
                }
            }
            invalidate()
        }
        return super.dispatchKeyEvent(event)
    }
}
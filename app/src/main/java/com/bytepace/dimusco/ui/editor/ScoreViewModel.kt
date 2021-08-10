package com.bytepace.dimusco.ui.editor

import android.graphics.*
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bytepace.dimusco.data.model.*
import com.bytepace.dimusco.data.model.Color
import com.bytepace.dimusco.data.model.Picture
import com.bytepace.dimusco.data.repository.LayerRepository
import com.bytepace.dimusco.data.repository.SettingsRepository
import com.bytepace.dimusco.data.repository.SyncRepository
import com.bytepace.dimusco.data.repository.UserRepository
import com.bytepace.dimusco.data.repository.score.ScoreRepository
import com.bytepace.dimusco.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.max

private const val LINE_THICKNESS_MIN = 20
private const val ERASER_THICKNESS_MIN = 100

class ScoreViewModel(
    private val sid: String,
    aid: String
) : ViewModel() {

    enum class EditMode {
        DRAW, TEXT, SYMBOL, ERASER
    }

    val showSizeControl = ObservableBoolean()
    val showLineThicknessControl = ObservableBoolean()

    val transparencyProgress = object : ObservableInt(1000) {
        override fun set(value: Int) {
            super.set(value)
            transparencyData.value = value / 1000f
        }
    }
    val lineThicknessProgress = object : ObservableInt(1000) {
        override fun set(value: Int) {
            super.set(value)
            lineThicknessData.value = (value + LINE_THICKNESS_MIN) / 1000f
        }
    }
    val eraserThicknessProgress = object : ObservableInt(1000) {
        override fun set(value: Int) {
            super.set(value)
            eraserThicknessData.value = (value + ERASER_THICKNESS_MIN) / 1000f
        }
    }
//    val layers = MediatorLiveData<List<LayerWithPage>>()
    val selectedMode: LiveData<EditMode>
        get() = selectedEditMode
    val transparency: LiveData<Float>
        get() = transparencyData
    val eraserThickness: LiveData<Float>
        get() = eraserThicknessData
    val lineThickness: LiveData<Float>
        get() = lineThicknessData
    val selectedSymbol: LiveData<Symbol>
        get() = selectedSymbolData
    val selectedSymbolIndex: LiveData<Int>
        get() = selectedSymbolIndexData
    val selectedColor: LiveData<Int>
        get() = selectedColorIndex
    val selectedColorValue: LiveData<Int>
        get() = selectedColorValueData
    val fontSize: LiveData<Float>
        get() = fontSizeData
    val onGetSettings: LiveData<Any>
        get() = onGetSettingsEvent
//    val onClickBack: LiveData<Any>
//        get() = onClickBackEvent
//    val onSaveChanges: LiveData<Any>
//        get() = onSaveChangesEvent
    val showSaveDialog: LiveData<Any>
        get() = showSaveDialogEvent
    val deletePicture: LiveData<Any>
        get() = deletePictureEvent
//    val activeLayer: LiveData<LayerWithPage?>
//        get() = activeLayerData
//    val pictures: LiveData<MutableList<Picture>>
//        get() = picturesData
//    val bottomLayers: LiveData<Bitmap?>
//        get() = bottomLayersBitmap
//    val topLayers: LiveData<Bitmap?>
//        get() = topLayersBitmap
//    val onPicturesChanged: LiveData<Any>
//        get() = picturesChangedEvent
    val score: LiveData<Score>

//    val pageBitmap: Bitmap?

    var colors: List<Color> = listOf()
    var symbols: List<Symbol> = listOf()

    private val selectedEditMode = MutableLiveData<EditMode>(null)
    private val selectedSymbolIndexData = MutableLiveData<Int>(null)
    private val selectedSymbolData = MutableLiveData<Symbol>(null)
    private val selectedColorIndex = MutableLiveData<Int>(null)
    private val selectedColorValueData = MutableLiveData(0)
    private val picturesData = MutableLiveData<MutableList<Picture>>(mutableListOf())
    private val transparencyData = MutableLiveData(1f)
    private val lineThicknessData = MutableLiveData(1f)
    private val eraserThicknessData = MutableLiveData(1f)
    private val fontSizeData = MutableLiveData(0.5f)
//    private val activeLayerData = MutableLiveData<LayerWithPage?>()
//    private val bottomLayersBitmap = MutableLiveData<Bitmap?>()
//    private val topLayersBitmap = MutableLiveData<Bitmap?>()
    private val onGetSettingsEvent = SingleLiveEvent<Any>()
    private val onClickBackEvent = SingleLiveEvent<Any>()
//    private val onSaveChangesEvent = SingleLiveEvent<Any>()
    private val showSaveDialogEvent = SingleLiveEvent<Any>()
    private val deletePictureEvent = SingleLiveEvent<Any>()
    private val picturesChangedEvent = SingleLiveEvent<Any>()

    private val settingsObserver = Observer<Settings> { onGetSettings(it) }
//    private val layersObserver = Observer<List<LayerWithPage>> { onLayersChanged(it) }
    private val editHistory: MutableList<EditorAction> = mutableListOf()

    private val settingsRepository by lazy { SettingsRepository.get() }
    private val scoreRepository by lazy { ScoreRepository.get() }
//    private val layerRepository by lazy { LayerRepository.get() }
    private val userRepository by lazy { UserRepository.get() }
    private val syncRepository by lazy { SyncRepository.get() }

//    private val visibleLayers: LiveData<List<Layer>>
//    private val pages: LiveData<List<LayerPage>>

    private var isPictureSelected = false
    private var confirmSavingChanges = true
    private var isImageDrawings = true
    private var isImageSymbols = true
    private var isImageText = true
    private var selectedColorIndexTemp = 0
    private var selectedTransparencyProgressTemp = 1000
    private var selectedLineThicknessTemp = 1f

    init {
        deleteFile(EDITOR_TEMP_FOLDER)
        settingsRepository.settings.observeForever(settingsObserver)
//        layers.observeForever(layersObserver)
        score = scoreRepository.getScore(aid)
//        pageBitmap = scoreRepository.getPageImage(page.id) //SameSources of Error
//        visibleLayers = layerRepository.getVisibleLayers(sid)
//        pages = layerRepository.getLayerPages(page.id)
//        layers.addSource(visibleLayers) { combineLayersWithPages() }
//        layers.addSource(pages) { combineLayersWithPages() }
    }

    fun onClickBack() {
        onClickBackEvent.call()
        if (editHistory.size > 0) {
            if (confirmSavingChanges) {
//                showSaveDialogEvent.call()
            } else {
//                onSaveChangesEvent.call()
            }
        } else {
            back()
        }
    }

    fun onClickUndo() {
        editHistory.lastIndex.let {
            if (it >= 0) {
                applyUndoAction(editHistory.removeAt(it))
            }
        }
    }

//    fun saveChanges(bitmap: Bitmap) {
//        pictures.value?.let {
//            savePageChanges(bitmap, it.toList())
//            back()
//        }
//    }

    fun back() {
//        MainNavigator.get().back()
    }

    fun selectSymbol(symbol: Symbol) {
        if (selectedSymbolIndexData.value == symbol.order) {
            selectedSymbolIndexData.value = null
            selectedSymbolData.value = null
            selectedEditMode.value = null
        } else {
            selectedSymbolIndexData.value = symbol.order
            selectedSymbolData.value = symbol
            selectedEditMode.value = EditMode.SYMBOL
        }
        showSizeControl.set(shouldShowSizeControl())
    }

    fun selectColor(position: Int) {
        selectedColorValueData.value = colors[position].value
        selectedColorIndex.value = position
    }

//    fun getLayerPageImage(filename: String): Bitmap? {
//        return layerRepository.getLayerImage(filename)
//    }

    fun setEditorMode(mode: EditMode?) {
        if (selectedEditMode.value == mode) {
            selectedEditMode.value = null
            showSizeControl.set(false)
        } else if (isPictureSelected && mode == EditMode.ERASER) {
            deletePictureEvent.call()
        } else {
            selectedSymbolIndexData.value = null
            selectedSymbolData.value = null
            selectedEditMode.value = mode
        }
        showLineThicknessControl.set(selectedEditMode.value == EditMode.DRAW)
        showSizeControl.set(shouldShowSizeControl())
    }

    fun onEditorAction(action: EditorAction) {
        editHistory.add(action)
    }

    fun onPictureSelected(picture: Picture) {
        if (!isPictureSelected) {
            selectedTransparencyProgressTemp = transparencyProgress.get()
            selectedLineThicknessTemp = lineThickness.value ?: 1f
            selectedColorIndexTemp = selectedColorIndex.value ?: 0
        }
        if (picture.type == PICTURE_TYPE_BRUSH) {
            lineThicknessProgress.set((picture.lineThickness * 1000).toInt() - LINE_THICKNESS_MIN)
        } else {
            lineThicknessProgress.set(1000 - LINE_THICKNESS_MIN)
        }
        transparencyProgress.set((picture.transparency * 1000).toInt())
        selectedColorValueData.value = picture.color
        selectedColorIndex.value = null
        showLineThicknessControl.set(picture.type == PICTURE_TYPE_BRUSH)
        showSizeControl.set(true)
        isPictureSelected = true
    }

    fun onPictureDeselected() {
        if (isPictureSelected) {
            selectedColorIndex.value = selectedColorIndexTemp
            selectedColorValueData.value = colors[selectedColorIndexTemp].value
            transparencyProgress.set(selectedTransparencyProgressTemp)
            lineThicknessProgress.set((selectedLineThicknessTemp * 1000).toInt() - LINE_THICKNESS_MIN)
        }
        setEditorMode(null)
        isPictureSelected = false
    }

    private fun applyUndoAction(action: EditorAction) {
        when (action.type) {
            EditorAction.Type.ADD -> picturesData.value?.removeAt(action.index!!)
            EditorAction.Type.EDIT -> picturesData.value?.set(action.index!!, action.picture!!)
            EditorAction.Type.REMOVE -> picturesData.value?.add(action.index!!, action.picture!!)
            EditorAction.Type.IMAGE -> {
                undoImagePictureEdit(action)
                return
            }
        }
        picturesChangedEvent.call()
    }

    private fun undoImagePictureEdit(action: EditorAction) {
        picturesData.value?.apply {
            indexOfFirst { it.type == PICTURE_TYPE_IMAGE }.let {
                when {
                    it >= 0 -> set(it, createImagePicture(action.filepath!!))
                    else -> add(createImagePicture(action.filepath!!))
                }
                picturesData.value = this
            }
        }
    }

    private fun shouldShowSizeControl(): Boolean {
        val mode = selectedEditMode.value
//        return (activeLayer.value?.layer?.isImageDrawings != true && mode == EditMode.DRAW) ||
          return mode == EditMode.SYMBOL || mode == EditMode.TEXT
    }

    private fun savePageChanges(bitmap: Bitmap, pictures: List<Picture>) {
//        val layer = activeLayerData.value?.layer ?: return
//
//        val width: Float
//        val height: Float
//        if (bitmap.width > bitmap.height) {
//            width = 1024f
//            height = 1024f * (bitmap.height.toFloat() / bitmap.width.toFloat())
//        } else {
//            height = 1024f
//            width = 1024f * (bitmap.width.toFloat() / bitmap.height.toFloat())
//        }
//
//        val layerPage = activeLayerData.value?.page ?: LayerPage(
//            layer.lid,
//            page.id,
//            width,
//            height,
//            mutableListOf()
//        )
//        pictures.forEach { it.updatedAt = System.currentTimeMillis() }
//        layerPage.pictures = mutableListOf<Picture>().apply {
//            if (layer.isImageText || layer.isImageSymbols || layer.isImageDrawings) {
//                add(createImagePicture(layerPage, bitmap))
//            }
//            addAll(pictures.filter { it.type != PICTURE_TYPE_IMAGE })
//        }
//
//        GlobalScope.launch {
//            layerRepository.updateLayerPage(layerPage)
//            if (layer.laid.isNotEmpty()) {
//                syncRepository.updateLayerPage(layerPage)
//            }
//        }
    }

    private fun createImagePicture(filepath: String): Picture {
        return Picture(type = PICTURE_TYPE_IMAGE, filePath = filepath, updatedAt = null)
    }

    private fun createImagePicture(
        layerPage: LayerPage,
        bitmap: Bitmap
    ): Picture { // TODO костыль с размерами
        val width: Float
        val height: Float
        if (bitmap.width > bitmap.height) {
            width = 1024f
            height = 1024f * (bitmap.height.toFloat() / bitmap.width.toFloat())
        } else {
            height = 1024f
            width = 1024f * (bitmap.width.toFloat() / bitmap.height.toFloat())
        }

        return Picture(
            width = width,
            height = height,
            type = PICTURE_TYPE_IMAGE,
            filePath = createPictureFilepath(layerPage),
            imageData = encodeImage(bitmap),
            updatedAt = System.currentTimeMillis()
        )
    }

    private fun createPictureFilepath(page: LayerPage): String {
        return "Scores/$sid/${userRepository.currentUser?.uid}/layerImages/${page.layerId}${page.paid}"
    }

    private fun combineLayersWithPages() {
//        val visibleLayers = visibleLayers.value
//        val pages = pages.value
//        if (visibleLayers != null && pages != null) {
//            val result = mutableListOf<LayerWithPage>()
//            visibleLayers.forEach { layer ->
//                result.add(LayerWithPage(layer, pages.find { it.layerId == layer.lid }))
//            }
//            layers.value = result
//        }
    }

    private fun onLayersChanged(layers: List<LayerWithPage>) {
        val activeLayer = layers.find { it.layer.isActive }
        val bottomLayers = mutableListOf<LayerWithPage>()
        val topLayers = mutableListOf<LayerWithPage>()

        if (activeLayer != null) {
            bottomLayers.addAll(layers.filter { layer -> layer.layer.position < activeLayer.layer.position })
            topLayers.addAll(layers.filter { layer -> layer.layer.position > activeLayer.layer.position })
        } else {
//            createLayer()
//            bottomLayers.addAll(layers)
        }

//        activeLayerData.value = activeLayer
//        picturesData.value = activeLayer?.page?.pictures?.toMutableList() ?: mutableListOf()
//        bottomLayersBitmap.value = getMergedLayersBitmap(bottomLayers)
//        topLayersBitmap.value = getMergedLayersBitmap(topLayers)
//        editHistory.clear()
    }

    // TODO format name timestamp according to iOS version
//    private fun createLayer() {
//        val ownerId = userRepository.currentUser?.uid ?: return
//        val name = "${userRepository.currentUser?.name}-${System.currentTimeMillis()}"
//
//        val layer = Layer(
//            "",
//            "L${UUID.randomUUID()}",
//            name,
//            LAYER_TYPE_READ_WRITE,
//            0,
//            10,
//            sid,
//            ownerId,
//            true,
//            isImageDrawings,
//            isImageSymbols,
//            isImageText,
//            true,
//            UUID.randomUUID().toString()
//        )

//        GlobalScope.launch {
//            layerRepository.createLayer(layer)
//            syncRepository.createLayer(layer)
//        }
//    }

    private fun getMergedLayersBitmap(layers: List<LayerWithPage>): Bitmap? {
//        pageBitmap?.let { pageBitmap ->
//            val multiplier = getScaleMultiplier(max(pageBitmap.width, pageBitmap.height))
//            Log.d("mutiplierbefore", multiplier.toString())
//            //      multiplier=12.00f
//            //Here comes the another crash
//            val bitmap = Bitmap.createBitmap(
//                pageBitmap.width,
//                pageBitmap.height,
//                Bitmap.Config.ARGB_8888
//            )
//            layers.forEach {
//                it.page?.pictures?.forEach { picture ->
//                    when (picture.type) {
//                        PICTURE_TYPE_IMAGE -> mergeImageBitmap(bitmap, picture)
//                        PICTURE_TYPE_BRUSH -> mergeDrawingBitmap(bitmap, picture, multiplier)
//                        PICTURE_TYPE_SYMBOL -> mergeSymbolBitmap(bitmap, picture, multiplier)
//                        PICTURE_TYPE_TEXT -> mergeTextBitmap(bitmap, picture, multiplier)
//                    }
//                }
//            }
//            return bitmap
//        }
        return null
    }

    private fun getScaleMultiplier(bitmapSize: Int): Float {
        return bitmapSize / LAYER_SIZE_MAX   //Layer isEditorImage unlike images you showing in list and pages
    }

    private fun mergeImageBitmap(bitmap: Bitmap, picture: Picture) {
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
//        getLayerPageImage(picture.filePath)?.let {
//            Canvas(bitmap).apply { drawBitmap(it, null, rect, null) }
//        }
    }

    private fun mergeDrawingBitmap(bitmap: Bitmap, picture: Picture, multiplier: Float) {
        val paint = Paint().apply {
            color = getOpaqueColor(picture.color)
            alpha = (picture.transparency * 255).toInt()
            strokeWidth = max(
                bitmap.width,
                bitmap.height
            ) / LAYER_SIZE_MAX * BASE_BRUSH_SIZE * picture.lineThickness
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        val path = Path()
        var cX: Float
        var cY: Float
        path.moveTo(picture.points[0].x, picture.points[0].y)
        cX = picture.points[0].x
        cY = picture.points[0].y
        picture.points.forEach {
            path.quadTo(cX, cY, (it.x + cX) / 2, (it.y + cY) / 2)
            cX = it.x
            cY = it.y
        }
        path.lineTo(cX, cY)

        val scaleMatrix = Matrix().apply {
            setScale(multiplier, multiplier, 0f, 0f)
        }
        path.transform(scaleMatrix)
        Canvas(bitmap).apply {
            drawPath(path, paint)
        }
    }

    private fun mergeSymbolBitmap(bitmap: Bitmap, picture: Picture, multiplier: Float): Bitmap? {
        getSymbolImage(picture.symbol)?.let {
            val rect = Rect(
                (picture.x * multiplier).toInt(),
                (picture.y * multiplier).toInt(),
                ((picture.x + picture.width) * multiplier).toInt(),
                ((picture.y + picture.height) * multiplier).toInt()
            )
            val paint = Paint().apply {
                colorFilter = PorterDuffColorFilter(
                    getOpaqueColor(picture.color),
                    PorterDuff.Mode.SRC_IN
                )
                alpha = (picture.transparency * 255).toInt()
                isAntiAlias = true
                isFilterBitmap = true
                isDither = true
            }
            Canvas(bitmap).apply { drawBitmap(it, null, rect, paint) }
        }
        return bitmap
    }

    private fun mergeTextBitmap(bitmap: Bitmap, picture: Picture, multiplier: Float) {
        val paint = Paint().apply {
            textSize = picture.fontSize * multiplier
            color = getOpaqueColor(picture.color)
            alpha = (picture.transparency * 255).toInt()
        }
        Canvas(bitmap).apply {
            drawText(
                picture.text,
                picture.x * multiplier,
                (picture.y + picture.fontSize) * multiplier,
                paint
            )
        }
    }

    private fun onGetSettings(settings: Settings) {
        settingsRepository.settings.removeObserver(settingsObserver)
        transparencyData.value = settings.transparency
        transparencyProgress.set((settings.transparency * 1000).toInt())
        lineThicknessData.value = settings.lineThickness
        lineThicknessProgress.set((settings.lineThickness * 1000).toInt() - LINE_THICKNESS_MIN)
        eraserThicknessData.value = settings.eraserThickness
        eraserThicknessProgress.set((settings.eraserThickness * 1000).toInt() - ERASER_THICKNESS_MIN)
        selectedColorValueData.value = settings.selectedColor
        selectedColorIndex.value = settings.colors.indexOfFirst { color ->
            color.value == settings.selectedColor
        }
        fontSizeData.value = settings.textSize
        confirmSavingChanges = settings.confirmSavingChanges
        isImageDrawings = settings.isImageDrawings
        isImageSymbols = settings.isImageSymbols
        isImageText = settings.isImageText
        colors = settings.colors
        symbols = settings.symbols
        onGetSettingsEvent.call()
    }

    override fun onCleared() {
        super.onCleared()
//        layers.removeObserver(layersObserver)
        deleteFile(EDITOR_TEMP_FOLDER)
    }
}

package com.bytepace.dimusco.data.manager

import android.content.Context
import android.graphics.*
import android.net.Uri
import com.bytepace.dimusco.common.file.createCacheFile
import com.bytepace.dimusco.common.file.saveAsPng
import com.bytepace.dimusco.data.repository.LayerRepository
import com.bytepace.dimusco.data.repository.score.ScoreRepository
import com.bytepace.dimusco.data.source.local.model.ComplexPage
import com.bytepace.dimusco.data.source.local.model.PathPointDB
import com.bytepace.dimusco.data.source.local.model.PictureDB
import com.bytepace.dimusco.data.source.local.model.ScorePageWindowDB
import com.bytepace.dimusco.utils.*
import kotlin.math.max

class PageFileManager(private val context: Context) {

    companion object {
        private const val CACHED_IMAGES_FOLDER = "images"
        private const val THUMBNAIL_FOLDER = "thumbnails"
        private const val ORIGINALS_FOLDER = "originals"
    }

    private val scoreRepository by lazy { ScoreRepository.get() }
    private val layerRepository by lazy { LayerRepository.get() }

    fun cacheThumbnailImage(complexPage: ComplexPage, shouldCrop: Boolean): String {
        val path = "$CACHED_IMAGES_FOLDER/$THUMBNAIL_FOLDER/${complexPage.page.filename}"
        val imageFile = context.createCacheFile(path)
        val image = getMergedPageBitmap(complexPage, shouldCrop)
        image?.saveAsPng(imageFile)
        image?.recycle()
        return Uri.fromFile(imageFile).toString()
    }

    fun cacheOriginalImage(complexPage: ComplexPage, shouldCrop: Boolean): String {
        val path = "$CACHED_IMAGES_FOLDER/$ORIGINALS_FOLDER/${complexPage.page.filename}"
        val imageFile = context.createCacheFile(path)
        val image = getMergedPageBitmap(complexPage, shouldCrop, false)
        image?.saveAsPng(imageFile)
        image?.recycle()
        return Uri.fromFile(imageFile).toString()
    }

    private fun getLayerPageImage(filename: String): Bitmap? {
        return layerRepository.getLayerImage(filename)
    }

    private fun getPageImage(filename: String, isThumbnail: Boolean): Bitmap? {
        return when (isThumbnail) {
            true -> scoreRepository.getPageThumbnail(filename)
            else -> scoreRepository.getPageImage(filename)
        }
    }

    private fun getMergedPageBitmap(
        page: ComplexPage,
        shouldCrop: Boolean,
        isThumbnail: Boolean = true
    ): Bitmap? {
        val bitmap = getPageImage(page.page.filename, isThumbnail)
            ?.run { copy(config, true) }
            ?: return null
        val canvas = Canvas(bitmap)
        val multiplier = max(bitmap.width, bitmap.height) / LAYER_SIZE_MAX
        page.pictures.sortedBy {
            it.pictureDB.order
        }.forEach { (picture, layer, list) ->
            when (picture.type) {
                PICTURE_TYPE_IMAGE -> mergeImageBitmap(canvas, picture)
                PICTURE_TYPE_BRUSH -> mergeDrawingBitmap(canvas, picture, list, multiplier)
                PICTURE_TYPE_SYMBOL -> mergeSymbolBitmap(canvas, picture, multiplier)
                PICTURE_TYPE_TEXT -> mergeTextBitmap(canvas, picture, multiplier)
            }
        }
        val offsetParams = page.cropWindows.lastOrNull().takeIf { shouldCrop }
        return when (offsetParams) {
            null -> bitmap
            else -> cropImage(bitmap, offsetParams)
        }
    }

    private fun cropImage(bitmap: Bitmap, offsets: ScorePageWindowDB): Bitmap {
        val offsetX = (offsets.xCoefficient * bitmap.width).toInt()
        val offsetY = (offsets.yCoefficient * bitmap.height).toInt()
        val width = (offsets.widthCoefficient * bitmap.width).toInt()
        val height = (offsets.heightCoefficient * bitmap.height).toInt()

        return Bitmap.createBitmap(bitmap, offsetX, offsetY, width, height)
    }

    private fun mergeImageBitmap(canvas: Canvas, picture: PictureDB) {
        val rect = Rect(0, 0, canvas.width, canvas.height)
        val bitmap = getLayerPageImage(picture.filePath) ?: return
        canvas.drawBitmap(bitmap, null, rect, null)
        bitmap.recycle()
    }

    private fun mergeDrawingBitmap(
        canvas: Canvas,
        picture: PictureDB,
        points: List<PathPointDB>,
        multiplier: Float
    ) {
        val paint = Paint().apply {
            color = getOpaqueColor(picture.color)
            alpha = (picture.transparency * 255).toInt()
            strokeWidth = max(
                canvas.width,
                canvas.height
            ) / LAYER_SIZE_MAX * BASE_BRUSH_SIZE * picture.lineThickness
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        val path = Path()
        var cX: Float
        var cY: Float
        path.moveTo(points[0].x, points[0].y)
        cX = points[0].x
        cY = points[0].y
        points.forEach {
            path.quadTo(cX, cY, (it.x + cX) / 2, (it.y + cY) / 2)
            cX = it.x
            cY = it.y
        }
        path.lineTo(cX, cY)

        val scaleMatrix = Matrix().apply {
            setScale(multiplier, multiplier, 0f, 0f)
        }
        path.transform(scaleMatrix)
        canvas.drawPath(path, paint)
    }

    private fun mergeSymbolBitmap(
        canvas: Canvas,
        picture: PictureDB,
        multiplier: Float
    ) {
        val symbol = getSymbolImage(picture.symbol) ?: return
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
        canvas.drawBitmap(symbol, null, rect, paint)
        symbol.recycle()
    }

    private fun mergeTextBitmap(canvas: Canvas, picture: PictureDB, multiplier: Float) {
        val paint = Paint().apply {
            textSize = picture.fontSize * multiplier
            color = getOpaqueColor(picture.color)
            alpha = (picture.transparency * 255).toInt()
        }

        val substrings = picture.text.split(
            delimiters = *arrayOf("\n"),
            ignoreCase = false,
            limit = 0
        )
        substrings.forEachIndexed { i, it ->
            canvas.drawText(
                it,
                picture.x * multiplier,
                (picture.y + picture.fontSize + picture.fontSize * i) * multiplier,
                paint
            )
        }
    }
}
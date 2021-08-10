package com.bytepace.dimusco.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Base64
import com.bytepace.dimusco.DimuscoApp
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.Deflater
import java.util.zip.Inflater
import kotlin.math.max

fun getSymbolImage(symbol: Int): Bitmap? {
    try {
        DimuscoApp.getContext().assets.open("symbols/symbol_$symbol.png").use {
            return BitmapFactory.decodeStream(it)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}

// TODO use two input bitmaps for better? memory efficiency
fun mergeBitmaps(bitmaps: List<Bitmap>): Bitmap? {
    if (bitmaps.isEmpty()) {
        return null
    }

    val result = Bitmap.createBitmap(bitmaps[0].width, bitmaps[0].height, bitmaps[0].config)
    val rect = Rect(0, 0, result.width, result.height)
    Canvas(result).apply {
        bitmaps.forEach { drawBitmap(it, null, rect, null) }
    }
    return result
}



fun getOptimizedImage(bytes: ByteArray): Bitmap {
    BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, this)
        inSampleSize = 2   //For Now lets keep it to 2 until client say something
        inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, this)
    }
}

fun decodeImage(base64Image: String): Bitmap {
    val bytes = Base64.decode(base64Image, Base64.DEFAULT)

    val inflater = Inflater(true)
    val outputStream = ByteArrayOutputStream()
    val buffer = ByteArray(8192)

    inflater.setInput(bytes)

    while (!inflater.finished()) {
        val count = inflater.inflate(buffer)
        outputStream.write(buffer, 0, count)
    }
    outputStream.close()
    inflater.end()
    val result = outputStream.toByteArray()

    return BitmapFactory.decodeByteArray(result, 0, result.size)
}

fun encodeImage(bitmap: Bitmap): String {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

    val deflater = Deflater(Deflater.DEFAULT_COMPRESSION, true)
    deflater.setInput(stream.toByteArray())
    deflater.finish()
    stream.close()

    val outputStream = ByteArrayOutputStream()
    val buffer = ByteArray(8192)
    while (!deflater.finished()) {
        outputStream.write(buffer, 0, deflater.deflate(buffer))
    }
    outputStream.close()
    deflater.end()

    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}

private fun getImageSizeMultiplier(imageSize: Int): Double {
    val target = with(DimuscoApp.getContext().resources.displayMetrics) {
        max(widthPixels, heightPixels).toDouble()
    }
    return imageSize / target
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}

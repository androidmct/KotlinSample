package com.bytepace.dimusco.common.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.InputStream

const val DEFAULT_QUALITY = 100

val DEFAULT_BITMAP_OPTIONS = BitmapFactory.Options().also {
    it.inSampleSize = 2
    it.inJustDecodeBounds = false
}

fun InputStream.asBitmap(options: BitmapFactory.Options = DEFAULT_BITMAP_OPTIONS): Bitmap? = use { `is` ->
    return BitmapFactory.decodeStream(this, null, options)
}

fun Bitmap.saveAsPng(file: File, quality: Int = DEFAULT_QUALITY) {
    file.outputStream().use { compress(Bitmap.CompressFormat.PNG, quality, it) }
}

fun Context.fileOf(filename: String) = File(filesDir, filename)

fun Context.createFile(filename: String): File {
    return File(filesDir, filename).also {
        it.parentFile?.mkdirs()
    }
}

fun Context.createCacheFile(filename: String): File {
    return File(cacheDir, filename).also {
        it.parentFile?.mkdirs()
    }
}
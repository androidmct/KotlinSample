package com.bytepace.dimusco.utils

import android.graphics.Bitmap
import com.bytepace.dimusco.DimuscoApp
import java.io.*

private const val FILE_ACCESS_MODE = "rw"
//Saving Bitmpa into local
//This class implements an output stream in which the data is written into a byte array. ...
// The data can be retrieved using toByteArray() and toString()
fun saveImage(filename: String, bitmap: Bitmap): Throwable? {
    val bytes = ByteArrayOutputStream().use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        return@use it.toByteArray()
    }
    return writeFile(filename, bytes)
}

fun saveImageThumbnail(filename: String, bitmap: Bitmap): Throwable? {
    val bytes = ByteArrayOutputStream().use {
        Bitmap.createScaledBitmap(
            bitmap,
            bitmap.width / 4,
            bitmap.height / 4,
            false
        ).compress(Bitmap.CompressFormat.PNG, 50, it)
        return@use it.toByteArray()
    }
    return writeFile("$THUMBNAILS_DIR${filename}", bytes)
}

fun writeFile(filename: String, bytes: ByteArray): Throwable? {
    var throwable: Throwable? = null

    try {
        val file = File(DimuscoApp.getContext().filesDir, filename)
        if (!file.exists()) file.parentFile?.mkdirs()
        RandomAccessFile(file, FILE_ACCESS_MODE).use { rFile ->
            deleteFile(file)

            val lock = rFile.channel.lock()

            ByteArrayInputStream(bytes).use { bais ->
                FileOutputStream(file).use { out ->
                    out.write(bais.readBytes())
                }
            }
            lock.release()
        }
    } catch (e: java.lang.Exception) {
        throwable = e
    } finally {
        return throwable
    }
}

fun readFile(filename: String): ByteArray? {
    val file = getFile(filename)
    if (!file.exists()) return null

    return FileInputStream(file).use { it.readBytes() }
}

fun deleteFile(filename: String): Boolean {
    return deleteFile(getFile(filename))
}

private fun deleteFile(file: File): Boolean {
    return try {
        when (file.isDirectory) {
            true -> file.deleteRecursively()
            else -> file.delete()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private fun getFile(filename: String): File {
    return File("${DimuscoApp.getContext().filesDir}/$filename")
}
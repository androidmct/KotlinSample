package com.bytepace.dimusco.data.manager

import android.content.Context
import android.graphics.Bitmap
import com.bytepace.dimusco.common.file.DEFAULT_QUALITY
import com.bytepace.dimusco.common.file.asBitmap
import com.bytepace.dimusco.common.file.createFile
import com.bytepace.dimusco.common.file.saveAsPng
import com.bytepace.dimusco.data.source.remote.api.DimuscoApi
import com.bytepace.dimusco.utils.THUMBNAILS_DIR

class DownloadPageManager(private val context: Context) {

    private val api = DimuscoApi.create()

    suspend fun saveFile(fileName: String, token: String) {
        val byteStream = api.requestPage(token, fileName).byteStream()
        val image = byteStream.asBitmap() ?: throw IllegalStateException()
        val thumbnail = Bitmap.createScaledBitmap(image, image.width / 2, image.height / 2, false)

        val imageFile = context.createFile(fileName)
        val thumbnailFile = context.createFile("$THUMBNAILS_DIR$fileName")

        image.saveAsPng(imageFile)
        image.recycle()
        thumbnail.saveAsPng(thumbnailFile, DEFAULT_QUALITY / 2)
        thumbnail.recycle()
    }

}
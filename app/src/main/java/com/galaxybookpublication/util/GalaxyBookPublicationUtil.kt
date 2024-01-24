package com.galaxybookpublication.util

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GalaxyBookPublicationUtil {
    fun saveBitmap(bitmap: Bitmap?): File? {
        var file: File? = null
        val pathName = "galaxyBookProfile" + System.currentTimeMillis()
        if (bitmap != null) {
            try {
                file = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + File.separator + pathName
                )
                file.createNewFile()

                var outputStream: FileOutputStream? = null
                try {
                    outputStream =
                        FileOutputStream(
                            Environment.getExternalStorageDirectory()
                                .toString() + File.separator + pathName
                        ) //here is set your file path where you want to save or also here you can set file object directly
                    bitmap.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        outputStream
                    ) // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        outputStream?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return file
    }
}
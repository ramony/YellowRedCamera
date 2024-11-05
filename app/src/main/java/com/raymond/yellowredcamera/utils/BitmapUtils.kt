package com.raymond.yellowredcamera.utils

import android.graphics.Bitmap
import android.graphics.Matrix

fun convertBitmapOld(bitmap: Bitmap, convertor: (Int) -> Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in pixels.indices) {
        val value = pixels[i]
        pixels[i] = convertor(value) or (value and 0xFF000000.toInt()) // 保留 alpha
    }

    val invertedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    invertedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return invertedBitmap
}

fun convertBitmap(bitmap: Bitmap, convertor: (Int) -> Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in pixels.indices) {
        val p = pixels[i]
        val value = p and 0xFFFFFF
        val alpha = p and 0xFF000000.toInt()
        pixels[i] = (convertor(value) and 0xFFFFFF) or alpha
    }

    val invertedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    invertedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return invertedBitmap
}

fun Bitmap.horiz(value: Int): Bitmap {
    if (value == 0) {
        return this;
    }
    val matrix = Matrix().apply { postScale(-1f, 1f) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

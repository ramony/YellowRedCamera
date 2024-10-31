package com.raymond.yellowredcamera

import android.graphics.Bitmap

fun convertBitmap(bitmap:Bitmap, convertor: (Int) -> Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in pixels.indices) {
        val value = pixels[i]
        pixels[i] = convertor(value) or 0xFF000000.toInt() // 保留 alpha
    }

    val invertedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    invertedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return invertedBitmap
}
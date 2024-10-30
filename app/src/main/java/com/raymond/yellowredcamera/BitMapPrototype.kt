package com.raymond.yellowredcamera

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.convert(convertor: (Int) -> Int): Bitmap {
    val bitmap = this;
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

//在图像上添加随机噪声，模拟复古效果。
fun Bitmap.vintageNoise(): Bitmap {
    return convert {
        val noise = ((Math.random() * 30) - 15).toInt() // 生成随机噪声
        val red = ((it shr 16 and 0xFF) + noise).coerceIn(0, 255)
        val green = ((it shr 8 and 0xFF) + noise).coerceIn(0, 255)
        val blue = ((it and 0xFF) + noise).coerceIn(0, 255)
        (it and 0xFF000000.toInt()) or (red shl 16) or (green shl 8) or blue
    }
}

//1. 颜色波动（Color Wobble）
fun Bitmap.colorWobble(): Bitmap {
    return convert {
        val red = (it shr 16 and 0xFF) + (Math.sin(it.toDouble() / 10) * 50).toInt()
        val green = (it shr 8 and 0xFF) + (Math.sin(it.toDouble() / 10 + Math.PI / 3) * 50).toInt()
        val blue = (it and 0xFF) + (Math.sin(it.toDouble() / 10 + 2 * Math.PI / 3) * 50).toInt()
         (it and 0xFF000000.toInt()) or
                (red.coerceIn(0, 255) shl 16) or
                (green.coerceIn(0, 255) shl 8) or
                blue.coerceIn(0, 255)
    }
}

//2. 黑白闪烁（B&W Flicker）
fun Bitmap.blackAndWhiteFlicker(): Bitmap {
    return convert {
        if (Math.random() > 0.5) {
            val gray = ((it shr 16 and 0xFF) + (it shr 8 and 0xFF) + (it and 0xFF)) / 3
              (it and 0xFF000000.toInt()) or (gray shl 16) or (gray shl 8) or gray
        } else {
            it
        }
    }
}


fun Bitmap.fixRedYellow(): Bitmap {
    return convert {
        val value = it and 0xFFFFFF
        if (isRed(value))
            0x0000FF
        else if (isGreen(value))
            0x00FF00
        else
            value
    }
}

fun Bitmap.invert(): Bitmap {
    return convert {
        it.inv()
    }
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}


fun Bitmap.cartoonEffect(): Bitmap {
    val bitmap = this;
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    val edgePixels = IntArray(width * height)

    // 边缘检测
    for (y in 1 until height - 1) {
        for (x in 1 until width - 1) {
            val gx = (-1 * pixels[(y - 1) * width + (x - 1)] + -2 * pixels[(y) * width + (x - 1)] + -1 * pixels[(y + 1) * width + (x - 1)]
                    + 1 * pixels[(y - 1) * width + (x + 1)] + 2 * pixels[(y) * width + (x + 1)] + 1 * pixels[(y + 1) * width + (x + 1)])
            val gy = (-1 * pixels[(y - 1) * width + (x - 1)] + -2 * pixels[(y - 1) * width + (x)] + -1 * pixels[(y - 1) * width + (x + 1)]
                    + 1 * pixels[(y + 1) * width + (x - 1)] + 2 * pixels[(y + 1) * width + (x)] + 1 * pixels[(y + 1) * width + (x + 1)])
            val edgeValue = Math.sqrt((gx * gx + gy * gy).toDouble()).toInt()
            edgePixels[y * width + x] = if (edgeValue > 128) 0xFF000000.toInt() else pixels[y * width + x]
        }
    }

    // 颜色简化
    for (i in edgePixels.indices) {
        val red = (edgePixels[i] shr 16 and 0xFF) / 64 * 64
        val green = (edgePixels[i] shr 8 and 0xFF) / 64 * 64
        val blue = (edgePixels[i] and 0xFF) / 64 * 64
        edgePixels[i] = (edgePixels[i] and 0xFF000000.toInt()) or (red shl 16) or (green shl 8) or blue
    }

    val cartoonBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    cartoonBitmap.setPixels(edgePixels, 0, width, 0, 0, width, height)
    return cartoonBitmap
}
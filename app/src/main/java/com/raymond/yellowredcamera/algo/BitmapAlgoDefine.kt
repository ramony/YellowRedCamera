package com.raymond.yellowredcamera.algo

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.raymond.yellowredcamera.utils.convertBitmap
import com.raymond.yellowredcamera.utils.isGreen
import com.raymond.yellowredcamera.utils.isRed
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random


//在图像上添加随机噪声，模拟复古效果。
fun vintageNoise(bitmap: Bitmap): Bitmap {
    return convertBitmap(bitmap) {
        val noise = ((Math.random() * 30) - 15).toInt() // 生成随机噪声
        val red = ((it shr 16 and 0xFF) + noise).coerceIn(0, 255)
        val green = ((it shr 8 and 0xFF) + noise).coerceIn(0, 255)
        val blue = ((it and 0xFF) + noise).coerceIn(0, 255)
        (it and 0xFF000000.toInt()) or (red shl 16) or (green shl 8) or blue
    }
}

//1. 颜色波动（Color Wobble）
fun colorWobble(bitmap: Bitmap): Bitmap {
    return convertBitmap(bitmap) {
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
fun blackAndWhiteFlicker(bitmap: Bitmap): Bitmap {
    return convertBitmap(bitmap) {
        if (Math.random() > 0.5) {
            val gray = ((it shr 16 and 0xFF) + (it shr 8 and 0xFF) + (it and 0xFF)) / 3
            (it and 0xFF000000.toInt()) or (gray shl 16) or (gray shl 8) or gray
        } else {
            it
        }
    }
}


fun fixRedYellow(bitmap: Bitmap): Bitmap {
    return convertBitmap(bitmap) {
        val value = it and 0xFFFFFF
        if (isRed(value))
            0x0000FF
        else if (isGreen(value))
            0x00FF00
        else
            value
    }
}


fun randomColorEffect(bitmap: Bitmap): Bitmap {
    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

    for (x in 0 until mutableBitmap.width) {
        for (y in 0 until mutableBitmap.height) {
            val pixel = mutableBitmap.getPixel(x, y)

            // 随机调整颜色
            val newRed = Random.nextInt(0, 256)
            val newGreen = Random.nextInt(0, 256)
            val newBlue = Random.nextInt(0, 256)

            val newColor = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
            mutableBitmap.setPixel(x, y, newColor)
        }
    }

    return mutableBitmap
}


fun invert(bitmap: Bitmap): Bitmap {
    return convertBitmap(bitmap) {
        it.inv()
    }
}

//fun Bitmap.rotate(degrees: Float): Bitmap {
//    val matrix = Matrix().apply { postRotate(degrees) }
//    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
//}

// 卡通效果
fun cartoonEffect(bitmap: Bitmap, edgeThreshold: Int = 30, colorLevels: Int = 5): Bitmap {
    // 先进行边缘检测
    val edgeImage = detectEdges(bitmap, edgeThreshold)
    // 然后进行色彩量化
    val quantizedImage = colorQuantization(bitmap, colorLevels)

    val width = bitmap.width
    val height = bitmap.height
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(width * height)

    // 合并边缘和量化后的图像
    for (i in pixels.indices) {
        val edge = Color.red(edgeImage.getPixel(i % width, i / width))
        if (edge < 128) {
            pixels[i] = quantizedImage.getPixel(i % width, i / width)
        } else {
            pixels[i] = Color.BLACK
        }
    }

    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

// 使用像素数组优化的灰度化效果
fun toGrayscale(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // 一次性获取所有像素
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    // 批量处理像素
    for (i in pixels.indices) {
        val pixel = pixels[i]
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)

        val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
        pixels[i] = Color.rgb(gray, gray, gray)
    }

    // 一次性设置处理后的像素
    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

// 使用像素数组优化的亮度调整
fun adjustBrightness(bitmap: Bitmap, factor: Double): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in pixels.indices) {
        val pixel = pixels[i]
        val r = Color.red(pixel)
        val g = Color.green(pixel)
        val b = Color.blue(pixel)

        val newR = min(255, max(0, (r * factor).toInt()))
        val newG = min(255, max(0, (g * factor).toInt()))
        val newB = min(255, max(0, (b * factor).toInt()))

        pixels[i] = Color.rgb(newR, newG, newB)
    }

    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

// 高斯模糊效果
fun gaussianBlur(bitmap: Bitmap, radius: Int = 3): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    val newPixels = pixels.clone()

    // 生成高斯核
    val kernel = generateGaussianKernel(radius)
    val kernelSize = radius * 2 + 1

    for (y in radius until height - radius) {
        for (x in radius until width - radius) {
            var red = 0.0
            var green = 0.0
            var blue = 0.0

            // 应用高斯核
            for (ky in -radius..radius) {
                for (kx in -radius..radius) {
                    val pixel = pixels[(y + ky) * width + (x + kx)]
                    val weight = kernel[ky + radius][kx + radius]

                    red += Color.red(pixel) * weight
                    green += Color.green(pixel) * weight
                    blue += Color.blue(pixel) * weight
                }
            }

            newPixels[y * width + x] = Color.rgb(
                red.toInt().coerceIn(0, 255),
                green.toInt().coerceIn(0, 255),
                blue.toInt().coerceIn(0, 255)
            )
        }
    }

    result.setPixels(newPixels, 0, width, 0, 0, width, height)
    return result
}

// 马赛克效果
fun mosaic(bitmap: Bitmap, blockSize: Int = 10): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    for (y in 0 until height step blockSize) {
        for (x in 0 until width step blockSize) {
            // 计算块的平均颜色
            var red = 0
            var green = 0
            var blue = 0
            var count = 0

            val maxY = min(y + blockSize, height)
            val maxX = min(x + blockSize, width)

            for (by in y until maxY) {
                for (bx in x until maxX) {
                    val pixel = pixels[by * width + bx]
                    red += Color.red(pixel)
                    green += Color.green(pixel)
                    blue += Color.blue(pixel)
                    count++
                }
            }

            val avgColor = Color.rgb(
                red / count,
                green / count,
                blue / count
            )

            // 填充块
            for (by in y until maxY) {
                for (bx in x until maxX) {
                    pixels[by * width + bx] = avgColor
                }
            }
        }
    }

    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

// 老照片效果
fun vintage(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in pixels.indices) {
        val pixel = pixels[i]
        var r = Color.red(pixel)
        var g = Color.green(pixel)
        var b = Color.blue(pixel)

        // 应用复古效果
        val newR = (r * 0.393 + g * 0.769 + b * 0.189).toInt().coerceIn(0, 255)
        val newG = (r * 0.349 + g * 0.686 + b * 0.168).toInt().coerceIn(0, 255)
        val newB = (r * 0.272 + g * 0.534 + b * 0.131).toInt().coerceIn(0, 255)

        pixels[i] = Color.rgb(newR, newG, newB)
    }

    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

// 油画效果
fun oilPainting(bitmap: Bitmap, radius: Int = 4, intensityLevels: Int = 20): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    val newPixels = pixels.clone()

    for (y in radius until height - radius) {
        for (x in radius until width - radius) {
            val intensityCount = IntArray(intensityLevels + 1)
            val redAverage = IntArray(intensityLevels + 1)
            val greenAverage = IntArray(intensityLevels + 1)
            val blueAverage = IntArray(intensityLevels + 1)

            // 分析邻域像素
            for (ky in -radius..radius) {
                for (kx in -radius..radius) {
                    val pixel = pixels[(y + ky) * width + (x + kx)]
                    val r = Color.red(pixel)
                    val g = Color.green(pixel)
                    val b = Color.blue(pixel)

                    val intensity = ((r + g + b) / 3.0 * intensityLevels / 255).toInt()
                    intensityCount[intensity]++
                    redAverage[intensity] += r
                    greenAverage[intensity] += g
                    blueAverage[intensity] += b
                }
            }

            // 找出最常见的强度级别
            var maxIndex = 0
            for (i in 1 until intensityLevels) {
                if (intensityCount[i] > intensityCount[maxIndex]) {
                    maxIndex = i
                }
            }

            // 计算平均颜色
            val count = intensityCount[maxIndex]
            if (count > 0) {
                newPixels[y * width + x] = Color.rgb(
                    redAverage[maxIndex] / count,
                    greenAverage[maxIndex] / count,
                    blueAverage[maxIndex] / count
                )
            }
        }
    }

    result.setPixels(newPixels, 0, width, 0, 0, width, height)
    return result
}

// 波浪扭曲效果
fun waveDistortion(bitmap: Bitmap, amplitude: Float = 20f, frequency: Float = 0.02f): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val xOffset = (amplitude * sin(y * frequency)).toInt()
            val sourceX = (x + xOffset).coerceIn(0, width - 1)
            result.setPixel(x, y, bitmap.getPixel(sourceX, y))
        }
    }

    return result
}

// 霓虹光效果
fun neonGlow(bitmap: Bitmap, glowRadius: Int = 3, glowStrength: Float = 1.5f): Bitmap {
    // 先进行边缘检测
    val edges = detectEdges(bitmap, 30)
    // 对边缘进行模糊处理
    val blurredEdges = gaussianBlur(edges, glowRadius)

    val width = bitmap.width
    val height = bitmap.height
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val originalColor = bitmap.getPixel(x, y)
            val edgeColor = blurredEdges.getPixel(x, y)

            val r = Color.red(originalColor)
            val g = Color.green(originalColor)
            val b = Color.blue(originalColor)

            val glowR = Color.red(edgeColor)
            val glowG = Color.green(edgeColor)
            val glowB = Color.blue(edgeColor)

            val newR = (r + glowR * glowStrength).toInt().coerceIn(0, 255)
            val newG = (g + glowG * glowStrength).toInt().coerceIn(0, 255)
            val newB = (b + glowB * glowStrength).toInt().coerceIn(0, 255)

            result.setPixel(x, y, Color.rgb(newR, newG, newB))
        }
    }

    return result
}

fun sharpen(bitmap: Bitmap): Bitmap {
    val sharpenMatrix = ColorMatrix(floatArrayOf(
        0f, -1f, 0f, 0f, 0f,
        -1f, 5f, -1f, 0f, 0f,
        0f, -1f, 0f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    ))

    val sharpenBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(sharpenMatrix)

    val canvas = Canvas(sharpenBitmap)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)

    return sharpenBitmap
}

// 彩色铅笔画效果
fun colorPencilSketch(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // 先进行边缘检测
    val edges = detectEdges(bitmap, 20)

    for (y in 0 until height) {
        for (x in 0 until width) {
            val originalColor = bitmap.getPixel(x, y)
            val edgeIntensity = Color.red(edges.getPixel(x, y)) / 255f

            val r = Color.red(originalColor)
            val g = Color.green(originalColor)
            val b = Color.blue(originalColor)

            val newR = (r * (1 - edgeIntensity)).toInt()
            val newG = (g * (1 - edgeIntensity)).toInt()
            val newB = (b * (1 - edgeIntensity)).toInt()

            result.setPixel(x, y, Color.rgb(newR, newG, newB))
        }
    }

    return result
}

// 镜像效果
fun mirror(bitmap: Bitmap, horizontal: Boolean = true): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    val newPixels = pixels.clone()

    if (horizontal) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                newPixels[y * width + x] = pixels[y * width + (width - 1 - x)]
            }
        }
    } else {
        for (y in 0 until height) {
            for (x in 0 until width) {
                newPixels[y * width + x] = pixels[(height - 1 - y) * width + x]
            }
        }
    }

    result.setPixels(newPixels, 0, width, 0, 0, width, height)
    return result
}

private fun generateGaussianKernel(radius: Int): Array<DoubleArray> {
    val size = radius * 2 + 1
    val kernel = Array(size) { DoubleArray(size) }
    val sigma = radius / 3.0
    var sum = 0.0

    for (y in -radius..radius) {
        for (x in -radius..radius) {
            val value = (1.0 / (2.0 * Math.PI * sigma * sigma)) *
                    Math.exp(-(x * x + y * y) / (2.0 * sigma * sigma))
            kernel[y + radius][x + radius] = value
            sum += value
        }
    }

    // 归一化
    for (y in kernel.indices) {
        for (x in kernel[0].indices) {
            kernel[y][x] /= sum
        }
    }

    return kernel
}


private fun detectEdges(bitmap: Bitmap, threshold: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(width * height)

    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    // Sobel算子
    val sobelX = arrayOf(
        intArrayOf(-1, 0, 1),
        intArrayOf(-2, 0, 2),
        intArrayOf(-1, 0, 1)
    )
    val sobelY = arrayOf(
        intArrayOf(-1, -2, -1),
        intArrayOf(0, 0, 0),
        intArrayOf(1, 2, 1)
    )

    for (y in 1 until height - 1) {
        for (x in 1 until width - 1) {
            var gx = 0
            var gy = 0

            for (i in -1..1) {
                for (j in -1..1) {
                    val pixel = pixels[(y + i) * width + (x + j)]
                    val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                    gx += gray * sobelX[i + 1][j + 1]
                    gy += gray * sobelY[i + 1][j + 1]
                }
            }

            val magnitude = Math.sqrt((gx * gx + gy * gy).toDouble()).toInt()
            val edge = if (magnitude > threshold) 255 else 0
            result.setPixel(x, y, Color.rgb(edge, edge, edge))
        }
    }

    return result
}

// 辅助方法：色彩量化
private fun colorQuantization(bitmap: Bitmap, levels: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(width * height)

    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in pixels.indices) {
        val r = Color.red(pixels[i])
        val g = Color.green(pixels[i])
        val b = Color.blue(pixels[i])

        val newR = ((r * levels / 256) * 256 / levels).coerceIn(0, 255)
        val newG = ((g * levels / 256) * 256 / levels).coerceIn(0, 255)
        val newB = ((b * levels / 256) * 256 / levels).coerceIn(0, 255)

        pixels[i] = Color.rgb(newR, newG, newB)
    }

    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

fun invByRect(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    val block = 100
    for (i in pixels.indices) {
        val p = pixels[i]
        val value = p and 0xFFFFFF
        val alpha = p and 0xFF000000.toInt()

        val n = (i % width / block) + (i / width / block)
        val newValue = if (n % 2 == 0) value.inv() else value
        pixels[i] = (newValue and 0xFFFFFF) or alpha//or 0xFF000000.toInt() // 保留 alpha
    }

    val invertedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    invertedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return invertedBitmap
}

fun invByCircle(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    val block = 50
    for (i in pixels.indices) {
        val p = pixels[i]
        val value = p and 0xFFFFFF
        val alpha = p and 0xFF000000.toInt()

        val n =  dis(i % width - width / 2, i / width  - height / 2)
        val newValue = if ((n / block ) % 2 == 0) value.inv() else value

        pixels[i] = (newValue and 0xFFFFFF) or alpha//or 0xFF000000.toInt() // 保留 alpha
    }

    val invertedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    invertedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return invertedBitmap
}

fun dis(x:Int, y:Int): Int {
    val d = x.toDouble().pow(2) + y.toDouble().pow(2)
    return sqrt(d).toInt();
}
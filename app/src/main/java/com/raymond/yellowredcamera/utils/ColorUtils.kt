package com.raymond.yellowredcamera.utils

fun isRed(color: Int): Boolean {
    val red = (color shr 16) and 0xFF
    val green = (color shr 8) and 0xFF
    val blue = color and 0xFF

    // 设置红色阈值，可以根据需要调整阈值
    val threshold = 100
    return red > 255 - threshold && green < threshold && blue < threshold
}

fun isGreen(color: Int): Boolean {
    val red = (color shr 16) and 0xFF
    val green = (color shr 8) and 0xFF
    val blue = color and 0xFF

    // 设置绿色阈值
    val threshold = 50
    return green > red + threshold && green > blue + threshold
}
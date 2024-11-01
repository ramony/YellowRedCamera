package com.raymond.yellowredcamera


object Constants {
    val bitmapAlgoes = listOf(
        BitmapAlgo(desc = "fixRedYellow") { fixRedYellow(it) },
        BitmapAlgo(desc = "cartoonEffect") { cartoonEffect(it) },
        BitmapAlgo(desc = "colorWobble") { colorWobble(it) },
        BitmapAlgo(desc = "mosaic") { mosaic(it) },
        BitmapAlgo(desc = "vintage") { vintage(it) },
        BitmapAlgo(desc = "oilPainting") { oilPainting(it) },
        BitmapAlgo(desc = "mirror") { mirror(it) },
        BitmapAlgo(desc = "waveDistortion") { waveDistortion(it) },
        BitmapAlgo(desc = "neonGlow") { neonGlow(it) },
        BitmapAlgo(desc = "colorPencilSketch") { colorPencilSketch(it) },
        BitmapAlgo(desc = "kaleidoscope") { kaleidoscope(it) },
        BitmapAlgo(desc = "colorSplit") { colorSplit(it) },
        BitmapAlgo(desc = "pixelSort") { pixelSort(it) },
        BitmapAlgo(desc = "glitch") { glitch(it) },
        BitmapAlgo(desc = "dream") { dream(it) },
        BitmapAlgo(desc = "gaussianBlur") { gaussianBlur(it) }
    )

}
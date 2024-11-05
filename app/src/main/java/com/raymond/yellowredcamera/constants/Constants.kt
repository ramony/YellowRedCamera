package com.raymond.yellowredcamera.constants

import com.raymond.yellowredcamera.algo.colorPencilSketch
import com.raymond.yellowredcamera.algo.colorWobble
import com.raymond.yellowredcamera.algo.fixRedYellow
import com.raymond.yellowredcamera.algo.invByCircle
import com.raymond.yellowredcamera.algo.mirror
import com.raymond.yellowredcamera.algo.mosaic
import com.raymond.yellowredcamera.algo.oilPainting
import com.raymond.yellowredcamera.algo.sharpen
import com.raymond.yellowredcamera.algo.vintage
import com.raymond.yellowredcamera.algo.waveDistortion
import com.raymond.yellowredcamera.entity.BitmapAlgo


object Constants {

    val BITMAP_ALGO_LIST = listOf(
        BitmapAlgo(desc = "fixRedYellow") { fixRedYellow(it) },
        BitmapAlgo(desc = "sharpen") { sharpen(it) },
        BitmapAlgo(desc = "invByCircle") { invByCircle(it) },
        BitmapAlgo(desc = "colorPencilSketch") { colorPencilSketch(it) },
        BitmapAlgo(desc = "colorWobble") { colorWobble(it) },
        BitmapAlgo(desc = "vintage") { vintage(it) },
        BitmapAlgo(desc = "waveDistortion") { waveDistortion(it) },
        BitmapAlgo(desc = "oilPainting") { oilPainting(it) },
        BitmapAlgo(desc = "mosaic") { mosaic(it) },
        BitmapAlgo(desc = "mirror") { mirror(it) },
    )

}
package com.raymond.yellowredcamera.entity

import android.graphics.Bitmap

data class BitmapAlgo(val desc: String, val apply: (Bitmap) -> Bitmap);

package com.raymond.yellowredcamera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import java.util.concurrent.Executors


@Composable
fun CameraEffectUI() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val context = LocalContext.current
        Permission(
            permission = Manifest.permission.CAMERA,
            requestPermissionTips = "You said you wanted a picture, so I'm going to have to ask for permission.",
            permissionNotAvailableContent = {
                Column() {
                    Text("O noes! No Camera!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        })
                    }) {
                        Text("Open Settings")
                    }
                }
            }
        ) {
            CameraEffectView()
        }
    }
}

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

@SuppressLint("RememberReturnType")
@Composable
fun CameraEffectView() {

    val context = LocalContext.current

    var cameraType by remember { mutableStateOf(0) }

    var algoType by remember { mutableStateOf(0) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var zoomRatio by remember { mutableStateOf(1f) }

    val cameraProvider by produceState<ProcessCameraProvider?>(initialValue = null) {
        value = context.getCameraProvider()
    }

    val cameraSelector = remember(cameraType) {
        val lensFacing =
            if (cameraType == 0)
                CameraSelector.LENS_FACING_BACK
            else
                CameraSelector.LENS_FACING_FRONT
        CameraSelector.Builder().requireLensFacing(lensFacing).build()
    }

    val preview = remember { Preview.Builder().build() }

    val lifecycleOwner = LocalLifecycleOwner.current
    val camera = remember(cameraProvider, cameraSelector) {
        cameraProvider?.let {
            val imageAnalysis =
                ImageAnalysis.Builder()
                    //  .setTargetResolution(android.util.Size(880, 360)) // 降低分辨率
                    .setOutputImageRotationEnabled(true) // 是否旋转分析器中得到的图片
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { ana ->
                        ana.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                            val invertedBitmap = imageProxy.toBitmap().run {
                                bitmapAlgoes[algoType].apply(this.horiz(cameraType))
                            }
                            bitmap = invertedBitmap
                            imageProxy.close()
                        }
                    }

            it.unbindAll()
            it.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
        }
    }

    LaunchedEffect(zoomRatio) {
        camera?.cameraControl?.setZoomRatio(zoomRatio)
    }

    Scaffold(modifier = Modifier.fillMaxWidth()) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Camera preview with inverted colors",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            // zoomRatio *= 1.5f;
                            Log.i("info", "zoomRatio $zoomRatio")
                        }
                        .pointerInput(this) {
                            detectTransformGestures { centroid, pan, zoom, rotation ->
                                Log.d(
                                    "info",
                                    "PointGestureScreen TransformGesture centroid: $centroid, pan: $pan, zoom: $zoom" +
                                            ", rotation: $rotation"
                                )
                                //  dragOffsetX += pan.x
                                //   dragOffsetY += pan.y
                                Log.i("info", "zoom $zoom")
                                zoomRatio *= zoom
                                if (zoomRatio < 1f) {
                                    zoomRatio = 1f
                                }
                                //   rotationAngle += rotation
                            }
                        }
                )
                val desc = bitmapAlgoes[algoType].desc
                Text(
                    "Select: $desc",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)

                ) {
                    ClickButton("Switch Camera") {
                        cameraType = (cameraType + 1) % 2
                    }
                    ClickButton("Switch Algo") {
                        algoType = (algoType + 1) % bitmapAlgoes.size
                    }
                }
            }
        }
    }
}


@Composable
fun ClickButton(text: String, clickFn: () -> Unit) {
    Button(
        onClick = {
            clickFn()
        }
    ) {
        Text(
            text,
        )
    }
}


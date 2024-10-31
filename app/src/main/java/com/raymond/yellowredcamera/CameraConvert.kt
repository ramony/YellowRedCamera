package com.raymond.yellowredcamera
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.graphics.Bitmap
//import android.net.Uri
//import android.provider.Settings
//import android.util.Log
//import androidx.camera.core.Camera
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.Preview
//import androidx.camera.view.PreviewView
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.unit.dp
//import java.util.concurrent.Executors
//
//
//@Composable
//fun CameraConvertUI() {
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.background
//    ) {
//        val context = LocalContext.current
//        Permission(
//            permission = Manifest.permission.CAMERA,
//            requestPermissionTips = "You said you wanted a picture, so I'm going to have to ask for permission.",
//            permissionNotAvailableContent = {
//                Column() {
//                    Text("O noes! No Camera!")
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Button(onClick = {
//                        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                            data = Uri.fromParts("package", context.packageName, null)
//                        })
//                    }) {
//                        Text("Open Settings")
//                    }
//                }
//            }
//        ) {
//            CameraConvert()
//        }
//    }
//}
//
//@SuppressLint("RememberReturnType")
//@Composable
//fun CameraConvert(
//    modifier: Modifier = Modifier,
//    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
//    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//) {
//    val convertList = listOf(
//        ConvertDO(desc = "fixRedYellow") { it.fixRedYellow() },
//        ConvertDO(desc = "cartoonEffect", convertor = { it.cartoonEffect() }),
//        ConvertDO(desc = "colorWobble", convertor = { it.colorWobble() })
//    )
//    var cameraType by remember { mutableStateOf(0) }
//
//    var convertType by remember { mutableStateOf(0) }
//    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
//    var zoomRatio by remember { mutableStateOf(1f) }
//
//    var camera by remember { mutableStateOf<Camera?>(null) }
//    val imageAnalysis = remember {
//        ImageAnalysis.Builder()
//            //  .setTargetResolution(android.util.Size(880, 360)) // 降低分辨率
//            .setOutputImageRotationEnabled(true) // 是否旋转分析器中得到的图片
//            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .build()
//            .also {
//                it.setAnalyzer(Executors.newSingleThreadExecutor(), { imageProxy ->
//                    val invertedBitmap = imageProxy.toBitmap().run {
//                        val o = this; //rotate(90f)
//                        convertList.get(convertType).convertor(o)
//                    }
//                    bitmap = invertedBitmap
//                    imageProxy.close()
//                })
//            }
//    }
//
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val context = LocalContext.current
//
//    LaunchedEffect(cameraType) {
//        val lensFacing =
//            if (cameraType == 0) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
//        val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
//        val cameraProvider = context.getCameraProvider()
//        val preview = Preview.Builder().build()
//        cameraProvider.unbindAll()
//        camera =
//            cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageAnalysis)
//        zoomRatio = 1f;
//    }
//
//    LaunchedEffect(zoomRatio) {
//        camera?.cameraControl?.setZoomRatio(zoomRatio)
//    }
//    Scaffold(modifier = Modifier.fillMaxWidth()) { innerPadding ->
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier
//                .padding(innerPadding)
//                .fillMaxWidth()
//                .background(Color.Black)
//        ) {
//            bitmap?.let {
//                Image(
//                    bitmap = it.asImageBitmap(),
//                    contentDescription = "Camera preview with inverted colors",
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .clickable {
//                            zoomRatio *= 1.5f;
//                            Log.i("info", "zoomRatio $zoomRatio")
//                        }
//                )
//                val desc = convertList[convertType].desc
//                Text(
//                    "Select: $desc",
//                    color = Color.White,
//                    modifier = Modifier
//                        .align(Alignment.TopCenter)
//                )
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(20.dp),
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//
//                ) {
//                    FnButton("Switch Camera") {
//                        cameraType = (cameraType + 1) % 2
//                    }
//                    FnButton("Switch Algo") {
//                        convertType = (convertType + 1) % convertList.size
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun FnButton(text: String, clickFn: () -> Unit) {
//    Button(
//        onClick = {
//            clickFn()
//        }
//    ) {
//        Text(
//            text,
//        )
//    }
//}

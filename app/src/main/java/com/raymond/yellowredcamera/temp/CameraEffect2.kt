package com.raymond.yellowredcamera.temp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.camera.core.CameraSelector
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raymond.yellowredcamera.model.CameraEffectModel
import com.raymond.yellowredcamera.icon.Circle
import com.raymond.yellowredcamera.icon.My
import com.raymond.yellowredcamera.constants.Constants
import com.raymond.yellowredcamera.view.createAnalysis
import com.raymond.yellowredcamera.prototype.getCameraProvider
import com.raymond.yellowredcamera.utils.Permission


@Composable
fun CameraEffect2UI() {
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
            CameraEffectView2()
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun CameraEffectView2() {
    val context = LocalContext.current

    val cameraEffectModel: CameraEffectModel = viewModel()
    val cameraType by cameraEffectModel.cameraType.collectAsState()
    val algoType by cameraEffectModel.algoType.collectAsState()
    val zoomRatio by cameraEffectModel.zoomRatio.collectAsState()

    var effectBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val cameraProvider by produceState<ProcessCameraProvider?>(initialValue = null) {
        value = context.getCameraProvider()
    }

    val cameraSelector = remember(cameraType) {
        val lensFacing = if (cameraType == 0)
            CameraSelector.LENS_FACING_BACK
        else
            CameraSelector.LENS_FACING_FRONT
        CameraSelector.Builder().requireLensFacing(lensFacing).build()
    }

    val preview = remember { Preview.Builder().build() }

    val lifecycleOwner = LocalLifecycleOwner.current
    val camera = remember(cameraProvider, cameraSelector) {
        cameraProvider?.let {
            val imageAnalysis = createAnalysis(cameraType) { bitmap ->
                val algo = Constants.BITMAP_ALGO_LIST[algoType]
                effectBitmap = algo.apply(bitmap)
            }
            it.unbindAll()
            it.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
        }
    }

    LaunchedEffect(camera, zoomRatio) {
        camera?.cameraControl?.setZoomRatio(zoomRatio)
    }

    CameraEffectView(effectBitmap, algoType,
        onZoom = { zoomValue ->
            cameraEffectModel.zoom(zoomValue)
        }, onSwitchCamera = {
            cameraEffectModel.switchCamera()
        }, onSwitchAlgo = { offset ->
            cameraEffectModel.switchAlgo(offset)
        })
}


@Composable
fun CameraEffectView(
    effectBitmap: Bitmap?, algoType: Int,
    onZoom: (Float) -> Unit, onSwitchCamera: () -> Unit, onSwitchAlgo: (Int) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxWidth()) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            effectBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Camera preview with inverted colors",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            // zoomRatio *= 1.5f;
                            //Log.i("info", "zoomRatio $zoomRatio")
                        }
                        .pointerInput(this) {
                            detectTransformGestures { centroid, pan, zoomValue, rotation ->
                                Log.d(
                                    "info",
                                    "PointGestureScreen TransformGesture centroid: $centroid, pan: $pan, zoom: $zoomValue" +
                                            ", rotation: $rotation"
                                )
                                //  dragOffsetX += pan.x
                                //   dragOffsetY += pan.y
                                Log.i("info", "zoomValue $zoomValue")
                                onZoom(zoomValue)
                                //   rotationAngle += rotation
                            }
                        }
                )
                val desc = Constants.BITMAP_ALGO_LIST[algoType].desc
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
                        .padding(30.dp)

                ) {
                    CameraIconButton(Icons.Outlined.Refresh) {
                        onSwitchCamera()
                    }

                    CameraIconButton(My.Circle) {

                    }

                    CameraIconButton(Icons.Outlined.KeyboardArrowUp, rotate = 270f) {
                        onSwitchAlgo(-1)
                    }

                    CameraIconButton(Icons.Outlined.KeyboardArrowDown, rotate = 270f) {
                        onSwitchAlgo(1)
                    }
                }
            }
        }
    }
}


@Composable
fun CameraIconButton(imageVector: ImageVector, rotate:Float = 0f, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(imageVector, contentDescription = null, tint = Color.Blue, modifier = Modifier.rotate(rotate).height(48.dp).width(48.dp))
    }
}

package com.raymond.yellowredcamera

import android.R
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.raymond.yellowredcamera.ui.theme.YellowRedCameraTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YellowRedCameraTheme {
                //TestUI()
                CameraEffectUI()
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YellowRedCameraTheme {
        val context = LocalContext.current
        val effectBitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_menu_camera)
        CameraEffectView(
            effectBitmap = effectBitmap, algoType = 0,
            onZoom = { }, onSwitchCamera = {}, onSwitchAlgo = {}
        )
    }
}


package com.raymond.yellowredcamera

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CameraEffectModel : ViewModel() {

    private var _cameraType = MutableStateFlow(0)
    val cameraType = _cameraType.asStateFlow()

    private var _algoType = MutableStateFlow(0)
    val algoType = _algoType.asStateFlow()

    private var _zoomRatio = MutableStateFlow(1f)
    val zoomRatio = _zoomRatio.asStateFlow()

    fun switchAlgo(offset: Int) {
        _algoType.update { v -> (v + offset + Constants.bitmapAlgoes.size) % Constants.bitmapAlgoes.size }
    }

    fun switchCamera() {
        zoomReset()
        _cameraType.update { v -> (v + 1) % 2 }
    }

    fun zoom(value: Float) {
        _zoomRatio.update { v -> if (v * value < 1f) 1f else v * value }
    }

    private fun zoomReset() {
        _zoomRatio.update { 1f }
    }

}
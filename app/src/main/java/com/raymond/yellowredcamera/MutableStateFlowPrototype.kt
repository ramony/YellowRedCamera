package com.raymond.yellowredcamera

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun <T> MutableStateFlow<MutableList<T>>.addToList(element: T) {
    this.update { list ->
        list.toMutableList().apply {
            this.add(element)
        }
    }
}

fun <T> MutableStateFlow<MutableList<T>>.modifyList(index: Int, convert: (T) -> T) {
    this.update { list ->
        list.toMutableList().apply {
            this[index] = convert(this[index])
        }
    }
}

fun <T> MutableStateFlow<MutableList<T>>.deleteFromList(index: Int) {
    this.update { list ->
        list.toMutableList().apply {
            this.removeAt(index)
        }
    }
}

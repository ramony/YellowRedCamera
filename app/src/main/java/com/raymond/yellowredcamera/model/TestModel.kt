package com.raymond.yellowredcamera.model

import androidx.lifecycle.ViewModel
import com.raymond.yellowredcamera.prototype.addToList
import com.raymond.yellowredcamera.prototype.deleteFromList
import com.raymond.yellowredcamera.prototype.modifyList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TestDO(val desc: String, val count: Int);


class TestModel : ViewModel() {

    private var _countList = MutableStateFlow(mutableListOf(TestDO("a", 0), TestDO("b", 0)))
    val countList = _countList.asStateFlow()

    fun inc(index: Int) {
        _countList.modifyList(index) {
            it.copy(count = it.count + 1)
        }
    }

    fun del(index: Int) {
        _countList.deleteFromList(index)
    }

    fun addNewCount() {
        _countList.addToList(TestDO("e" + (Math.random() * 100).toInt().toString(), 0));
    }
}


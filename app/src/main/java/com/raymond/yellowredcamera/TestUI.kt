package com.raymond.yellowredcamera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun TestUI() {
    val testModel: TestModel = viewModel()
    val countList by testModel.countList.collectAsState()

    Scaffold(modifier = Modifier.fillMaxWidth()) { innerPadding ->
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            LazyColumn {
                itemsIndexed(countList) { index, it ->
                    TestView(it.desc + " " + it.count, onCount = {
                        testModel.inc(index)
                    }) {
                        testModel.del(index)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Button(
                    onClick = { testModel.addNewCount() }
                ) {
                    Text(
                        "addNewCount",
                    )
                }

                NewButton()
            }
        }
    }
}

@Composable
fun NewButton() {
    val testModel: TestModel = viewModel()

    Button(
        onClick = { testModel.addNewCount() }
    ) {
        Text(
            "addNewButton",
        )
    }
}

@Composable
fun TestView(text: String, onCount: () -> Unit, onDelete: () -> Unit) {
    Row(modifier = Modifier.height(100.dp)) {
        Text(
            text,
            modifier = Modifier.fillMaxHeight()
        )
        Button(
            onClick = onCount
        ) {
            Text(
                "increase 1",
            )
        }

        Button(
            onClick = onDelete
        ) {
            Text(
                "delete",
            )
        }

    }
}

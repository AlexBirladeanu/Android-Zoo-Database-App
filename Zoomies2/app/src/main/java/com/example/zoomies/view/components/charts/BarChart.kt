package com.example.zoomies.view.components.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val defaultMaxHeight = 200.dp


@Composable
internal fun BarChart(
    modifier: Modifier = Modifier,
    values: List<Float>,
    valuesNames: List<String>,
    maxHeight: Dp = defaultMaxHeight
) {

    assert(values.isNotEmpty()) { "Input values are empty" }
    val primaryColor = MaterialTheme.colors.primary
    val borderColor = MaterialTheme.colors.primary
    val density = LocalDensity.current
    val strokeWidth = with(density) { 1.dp.toPx() }

    var isSelectedList by remember { mutableStateOf(values.map{false}.toMutableList()) }
    if (values.size != isSelectedList.size) {
        isSelectedList = values.map { false }.toMutableList()
    }
    var message by remember{mutableStateOf("")}
    Column {
        Row(
            modifier = modifier.then(
                Modifier
                    .fillMaxWidth()
                    .height(maxHeight)
                    .drawBehind {
                        // draw X-Axis
                        drawLine(
                            color = borderColor,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = strokeWidth
                        )
                        // draw Y-Axis
                        drawLine(
                            color = borderColor,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = strokeWidth
                        )
                    }
            ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            values.forEachIndexed { index, item ->
                Bar(
                    value = item,
                    color = if (isSelectedList[index]) Color.White else primaryColor,
                    maxHeight = maxHeight,
                    modifier = Modifier
                        .clickable {
                            isSelectedList[index] = true
                            message = (item / 30).toInt().toString() + " " + valuesNames[index]
                            isSelectedList.forEachIndexed { i, b ->
                                if (i != index) {
                                    isSelectedList[i] = false
                                }
                            }
                        }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
            )
        }
    }
}

@Composable
private fun RowScope.Bar(
    modifier: Modifier,
    value: Float,
    color: Color,
    maxHeight: Dp
) {

    val itemHeight = remember(value) { value * maxHeight.value / 100 }


    Spacer(
        modifier = modifier
            .padding(horizontal = 5.dp)
            .height(itemHeight.dp)
            .weight(1f)
            .background(color)
    )

}
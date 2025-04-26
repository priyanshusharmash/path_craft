package com.metaminds.pathcraft.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.metaminds.pathcraft.ui.theme.PathCraftTheme
import kotlin.collections.listOf

@Composable
fun RoadMapChart(
    modifier: Modifier = Modifier,
    topicList: List<String>
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {
        Box(modifier = Modifier
            .padding(
                top = 10.dp,
                bottom = 10.dp,
                start = 10.dp,
                end = 70.dp
            )
            .clip(RoundedCornerShape(10.dp))
            .background(color = Color.Green),) {
            Column(
                modifier=Modifier.padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                topicList.forEachIndexed { index, topic ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier=Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(color=Color.Yellow)
                            .border(color = Color.Black, shape = RoundedCornerShape(10.dp), width = 1.dp)) {
                            Text(
                                text = topic,
                                textAlign = TextAlign.Center,
                                modifier=Modifier
                                    .padding(10.dp)
                                )
                        }
                        if(index != topicList.size-1) {
                            Spacer(
                                Modifier
                                    .height(40.dp)
                                    .width(1.dp)
                                    .background(color = Color.Black)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoadMapChartPreview() {
    PathCraftTheme {
        RoadMapChart(topicList = listOf("a", "b", "c", "d", "e"))
    }
}
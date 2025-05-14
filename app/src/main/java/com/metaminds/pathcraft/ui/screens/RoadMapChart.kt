package com.metaminds.pathcraft.ui.screens

import android.util.Log
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.theme.PathCraftTheme
import com.metaminds.pathcraft.ui.viewModels.ChatScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.listOf
import kotlin.collections.mutableListOf

@Composable
fun RoadMapChart(
    modifier: Modifier = Modifier,
    viewModel: ChatScreenViewModel = viewModel(factory= AppViewModelProvider.factory)
) {
    val delayedTopics by viewModel.delayedList.collectAsState()
    Box(
        modifier = modifier.fillMaxWidth().animateContentSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Box(modifier = Modifier
            .padding(
                top=10.dp,
                bottom = 10.dp,
                start = 10.dp,
                end = 70.dp
            )
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.primaryContainer),) {
            Column(
                modifier=Modifier.padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                delayedTopics.forEachIndexed { index, topic ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier=Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(color= MaterialTheme.colorScheme.primary)
                            .border(color = Color.Black, shape = RoundedCornerShape(10.dp), width = 1.dp)) {
                            Text(
                                text = topic,
                                textAlign = TextAlign.Center,
                                modifier=Modifier
                                    .padding(10.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                                )
                        }
                        if(index != delayedTopics.size-1) {
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

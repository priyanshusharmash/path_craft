package com.metaminds.pathcraft.ui.screens.courseScreens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metaminds.pathcraft.NOTES
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.screens.ShowLoadingScreen
import com.metaminds.pathcraft.ui.viewModels.CourseCheckpoint
import com.metaminds.pathcraft.ui.viewModels.CourseFetchStatus
import com.metaminds.pathcraft.ui.viewModels.CourseScreenViewModel

object NotesTabScreenNavigationDestination : NavigationDestination {
    override val titleRes: Int? = null
    override val route: String = NOTES
}

@Composable
fun NotesTabScreen(
    modifier: Modifier = Modifier,
    viewModel: CourseScreenViewModel = viewModel(factory = AppViewModelProvider.factory),
    navigateToNotesContentScreen: (String, String) -> Unit
) {
    val uiState = viewModel.courseUiState
    val context = LocalContext.current
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is CourseFetchStatus.Loading -> {
                ShowLoadingScreen()
            }

            is CourseFetchStatus.Success -> {
                NotesTabScreenBody(
                    topicList = uiState.topicList,
                    onTopicClick = { topic, subtopic ->
                        navigateToNotesContentScreen(topic, subtopic)
                    },
                    viewModel = viewModel
                )
            }

            is CourseFetchStatus.Error -> {
                Toast.makeText(context, uiState.error, Toast.LENGTH_SHORT).show()
            }

            is CourseFetchStatus.Waiting -> {
                CircularProgressIndicator()
            }
        }

    }
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
private fun NotesTabScreenBody(
    modifier: Modifier = Modifier,
    topicList: List<CourseCheckpoint>,
    onTopicClick: (String, String) -> Unit,
    viewModel: CourseScreenViewModel
) {
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = viewModel.scrollPosition.collectAsState().value
    )
    LaunchedEffect(lazyListState.firstVisibleItemIndex) {
        viewModel.updateScrollPosition(lazyListState.firstVisibleItemIndex)
    }
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        itemsIndexed(items = topicList) { index, topic ->
            var expandedList = viewModel.expandedItemList
            CheckPointCard(
                topicName = topic.checkpoint,
                subtopicList = topic.subTopics,
                cardColors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.inverseSurface,
                ),
                onTopicClick = { subtopic ->
                    onTopicClick(topic.checkpoint, subtopic)
                },
                subTopicsShown = viewModel.expandedItemList.contains(index),
                changeSubTopicState = {
                    if (expandedList.contains(index)) expandedList.remove(index)
                    else expandedList.add(index)
                }
            )
        }
    }
}

@Composable
private fun CheckPointCard(
    modifier: Modifier = Modifier,
    topicName: String,
    subtopicList: List<String>,
    cardColors: CardColors,
    onTopicClick: (String) -> Unit,
    subTopicsShown: Boolean,
    changeSubTopicState: (Boolean) -> Unit
) {
    Card(
        colors = cardColors,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape= RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .clickable { changeSubTopicState(!subTopicsShown) }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = topicName,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    modifier=Modifier.weight(1f)
                )
                IconButton(
                    onClick = { changeSubTopicState(!subTopicsShown) }
                ) {
                    Icon(
                        modifier = Modifier.width(50.dp)
                            .rotate(if (subTopicsShown) 180F else 0F)
                            .animateContentSize(),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
            if (subTopicsShown) {
                SubTopicBox(
                    modifier = Modifier,
                    subtopicList = subtopicList,
                    onTopicClick = onTopicClick
                )
            }
        }
    }
}

@Composable
private fun SubTopicBox(
    modifier: Modifier = Modifier,
    subtopicList: List<String>,
    onTopicClick: (String) -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(PaddingValues(horizontal = 8.dp, vertical = 8.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            subtopicList.forEachIndexed { index, subtopic ->
                Row(
                    modifier = Modifier.clickable { onTopicClick(subtopic) },
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 10.dp, horizontal = 5.dp),
                        text = subtopic,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                        textAlign = TextAlign.Justify,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

package com.metaminds.pathcraft.ui.screens.courseScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.metaminds.pathcraft.ui.AppViewModelProvider
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.screens.ShowLoadingScreen
import com.metaminds.pathcraft.ui.theme.PathCraftTheme
import com.metaminds.pathcraft.ui.viewModels.CourseCheckpoint
import com.metaminds.pathcraft.ui.viewModels.CourseFetchStatus
import com.metaminds.pathcraft.ui.viewModels.CourseScreenViewModel

object NotesTabScreenNavigationDestination: NavigationDestination{
    override val titleRes: Int? = null
    override val route: String = "notes"
}

@Composable
fun NotesTabScreen(
    modifier: Modifier = Modifier,
    viewModel: CourseScreenViewModel=viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState=viewModel.courseUiState
    val context = LocalContext.current
    Box(
        modifier=modifier.fillMaxSize()
    ){
        when(uiState){
            is CourseFetchStatus.Loading -> {
                ShowLoadingScreen()
            }
            is CourseFetchStatus.Success -> {
                NotesTabScreenBody(
                    topicList = uiState.topicList
                )
            }
            is CourseFetchStatus.Error -> {
                Toast.makeText(context,uiState.error,Toast.LENGTH_SHORT).show()
            }
        }

    }
}

@Composable
private fun NotesTabScreenBody(
    modifier: Modifier = Modifier,
    topicList: List<CourseCheckpoint>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
    ){
        itemsIndexed (items=topicList){index,topic->
            CheckPointCard(
                topicName = topic.checkpoint,
                subtopicList = topic.subTopics,
                cardColors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.inverseSurface
                )
            )
        }
    }
}

@Composable
fun CheckPointCard(
    modifier: Modifier = Modifier,
    topicName: String,
    subtopicList: List<String>,
    cardColors: CardColors
) {
    Card (
        colors=cardColors,
        border = CardDefaults.outlinedCardBorder(true),
        modifier=modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Column(
            modifier=Modifier.padding(8.dp)
        ) {
            Text(
                text = topicName,
                style = MaterialTheme.typography.titleMedium
            )
            SubTopicBox(
                modifier=Modifier,
                subtopicList = subtopicList
            )
        }
    }
}

@Composable
fun SubTopicBox(
    modifier: Modifier = Modifier,
    subtopicList:List<String>
) {
    Box(
        modifier=modifier.fillMaxWidth()
    ){
        Column(
            modifier=Modifier.padding(PaddingValues(horizontal = 8.dp, vertical = 8.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            subtopicList.forEachIndexed { index,subtopic->
                Row (
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null
                    )
                    Text(
                        modifier=Modifier
                            .fillMaxWidth()
                            .background(
                                color= MaterialTheme.colorScheme.tertiaryContainer,
                                shape= RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 10.dp, horizontal = 5.dp),
                        text= subtopic,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                        textAlign = TextAlign.Justify,
                        color=MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SubTopicBoxPrev () {
    PathCraftTheme {
        SubTopicBox(subtopicList = listOf("a","b","c"))
    }
}
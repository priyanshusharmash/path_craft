package com.metaminds.pathcraft.ui.screens.courseScreens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.metaminds.pathcraft.COURSE_RESOURCE
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.viewModels.CourseFetchStatus
import com.metaminds.pathcraft.ui.viewModels.CourseScreenViewModel


object CourseBooksResourcesTabNavigationDestination: NavigationDestination{
    override val titleRes: Int? = null
    override val route: String = COURSE_RESOURCE
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun CourseResourcesTab(
   modifier: Modifier = Modifier,
   viewModel: CourseScreenViewModel
) {
    val uiState=viewModel.courseUiState
    val context= LocalContext.current
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = viewModel.scrollPositionForContent.collectAsState().value
    )
    LaunchedEffect(lazyListState.firstVisibleItemIndex) {
        viewModel.updateScrollPositionForContent(lazyListState.firstVisibleItemIndex)
    }
    Box(
        modifier=modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        when(uiState){
            is CourseFetchStatus.Waiting -> {
                CircularProgressIndicator()
            }
            is CourseFetchStatus.Loading -> {
                CircularProgressIndicator()
            }
            is CourseFetchStatus.Error -> {
                Toast.makeText(context,uiState.error,Toast.LENGTH_SHORT).show()
            }
            is CourseFetchStatus.Success -> {
              ScreenBody(viewModel=viewModel, uiState = uiState,state=lazyListState)
            }
        }
    }
}


@Composable
private fun ScreenBody(
    modifier: Modifier = Modifier,
    uiState: CourseFetchStatus.Success,
    viewModel: CourseScreenViewModel,
    state: LazyListState
) {
    LazyColumn (
        modifier=modifier,
        state=state,
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        itemsIndexed (items=uiState.otherReferences){index,content->
            TopicCard(
                topicName = content.contentName,
                cardColors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.inverseSurface,
                ),
                descriptionVisibility =viewModel.expandedItemListForContent.contains(index) ,
                changeDescriptionVisibility ={
                    if(viewModel.expandedItemListForContent.contains(index)) viewModel.expandedItemListForContent.remove(index)
                    else viewModel.expandedItemListForContent.add(index)},
                description = content.contentDescription
            )
        }
    }
}

@Composable
private fun TopicCard(
    modifier: Modifier = Modifier,
    topicName: String,
    cardColors: CardColors,
    descriptionVisibility: Boolean,
    changeDescriptionVisibility: (Boolean) -> Unit,
    description:String,
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
                    .clickable { changeDescriptionVisibility(!descriptionVisibility) }
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
                    onClick = { changeDescriptionVisibility(!descriptionVisibility) }
                ) {
                    Icon(
                        modifier = Modifier.width(50.dp)
                            .rotate(if (descriptionVisibility) 180F else 0F)
                            .animateContentSize(),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
            if (descriptionVisibility) {
                Text(text = description)
            }
        }
    }
}
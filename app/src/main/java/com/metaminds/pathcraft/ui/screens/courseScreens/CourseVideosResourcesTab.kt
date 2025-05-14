package com.metaminds.pathcraft.ui.screens.courseScreens

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.viewModels.CourseFetchStatus
import com.metaminds.pathcraft.ui.viewModels.CourseScreenViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object CourseVideosResourcesTabNavigationDestination: NavigationDestination{
    override val titleRes: Int? = null
    override val route: String = "course_videos"
}

@Composable
fun CourseVideosResourcesTab(
    modifier: Modifier = Modifier,
    viewModel: CourseScreenViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(300)
        viewModel.refreshState()
    }
    Box(
        modifier=modifier.fillMaxSize()
    ){
        val uiState=viewModel.courseUiState
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
                ScreenBody(
                    viewModel=viewModel,
                )
            }
        }
    }
}

@Composable
private fun ScreenBody(
    modifier: Modifier = Modifier,
    viewModel: CourseScreenViewModel,
) {
    /*
    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        /*
        itemsIndexed(items=uiState.topicList) {index,topic->
            /*
            TopicCard(
                topicName = topic.checkpoint,
                cardColors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.inverseSurface,
                ),
                videoVisibility = viewModel.expandedItemListForVideos.contains(index) ,
                changeVideoVisibility = { checkpoint, visibility ->
                    coroutineScope.launch{
                        viewModel.generateVideoId(checkpoint)
                    }
                        if (visibility) viewModel.expandedItemListForVideos.add(index)
                        else viewModel.expandedItemListForVideos.remove(index)
                },
                videoId = viewModel.currentVideoId
            )

             */

        }

         */
    }

     */
    YoutubePlayer(videoId = viewModel.currentVideoId, lifecycleOwner = LocalLifecycleOwner.current)
}

@Composable
private fun TopicCard(
    modifier: Modifier = Modifier,
    topicName: String,
    cardColors: CardColors,
    videoVisibility: Boolean,
    changeVideoVisibility: (String,Boolean) -> Unit,
    videoId:String,
) {
    Card(
        colors = cardColors,
        border = CardDefaults.outlinedCardBorder(true),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .clickable { changeVideoVisibility(topicName,!videoVisibility) }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = topicName,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(
                    onClick = { changeVideoVisibility(topicName,!videoVisibility) }
                ) {
                    Icon(
                        modifier = Modifier
                            .rotate(if (videoVisibility) 180F else 0F)
                            .animateContentSize(),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
            if (videoVisibility) {
                YoutubePlayer(videoId = videoId, lifecycleOwner = LocalLifecycleOwner.current)
            }
        }
    }
}

@Composable
fun YoutubePlayer(
    modifier: Modifier = Modifier,
    videoId: String,
    lifecycleOwner: LifecycleOwner
) {
    AndroidView(modifier=modifier,factory = {it->
        YouTubePlayerView(context = it).apply {
            lifecycleOwner.lifecycle.addObserver(this)
            addYouTubePlayerListener(object: AbstractYouTubePlayerListener(){
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoId,0f)

                }
            })
        }
    })
}
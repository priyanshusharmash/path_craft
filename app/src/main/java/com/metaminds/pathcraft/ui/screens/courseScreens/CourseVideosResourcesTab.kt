package com.metaminds.pathcraft.ui.screens.courseScreens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.metaminds.pathcraft.COURSE_VIDEOS
import com.metaminds.pathcraft.ui.navigation.NavigationDestination
import com.metaminds.pathcraft.ui.viewModels.CourseFetchStatus
import com.metaminds.pathcraft.ui.viewModels.CourseScreenViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

object CourseVideosResourcesTabNavigationDestination: NavigationDestination{
    override val titleRes: Int? = null
    override val route: String = COURSE_VIDEOS
}

@Composable
fun CourseVideosResourcesTab(
    modifier: Modifier = Modifier,
    viewModel: CourseScreenViewModel
) {
    val context = LocalContext.current
    Box(
        modifier=modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
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
               YoutubePlayer(videoId = uiState.currentVideoId, lifecycleOwner = LocalLifecycleOwner.current)
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
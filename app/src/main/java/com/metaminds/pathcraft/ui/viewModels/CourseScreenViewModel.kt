package com.metaminds.pathcraft.ui.viewModels

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metaminds.pathcraft.data.AppRepository
import com.metaminds.pathcraft.network.VideoId
import com.metaminds.pathcraft.network.VideoItem
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseScreenNavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: AppRepository
) : ViewModel() {
    private val encodedTitle: String =
        checkNotNull(savedStateHandle[CourseScreenNavigationDestination.COURSE])
    val title: String = Uri.decode(encodedTitle)
    var courseUiState: CourseFetchStatus by mutableStateOf(CourseFetchStatus.Loading)
    var expandedItemList = mutableStateListOf<Int>()
    private val _scrollPosition = MutableStateFlow(0)
    val scrollPosition = _scrollPosition.asStateFlow()
    var currentVideoId by mutableStateOf("")
        private set

    fun updateScrollPosition(index: Int) {
        _scrollPosition.value = index
    }

    fun refreshState() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                courseUiState= CourseFetchStatus.Success(
                    topicList = repository.getCourseCheckpoints(title)
                )
                currentVideoId =
                    repository.fetchYoutubeVideoLink(title) ?: repository.saveYoutubeVideoId(title)
            }catch (e: Exception){
                CourseFetchStatus.Error(e.message.toString())
            }
        }
    }

}

sealed class CourseFetchStatus {
    data class Success(val topicList: List<CourseCheckpoint>) : CourseFetchStatus()
    object Loading : CourseFetchStatus()
    data class Error(val error: String) : CourseFetchStatus()
    object Waiting : CourseFetchStatus()
}
package com.metaminds.pathcraft.ui.viewModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metaminds.pathcraft.data.AppRepository
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseScreenNavigationDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: AppRepository
) : ViewModel() {
    private val encodedTitle: String =
        checkNotNull(savedStateHandle[CourseScreenNavigationDestination.COURSE])
    val title: String = Uri.decode(encodedTitle)
    var courseUiState: CourseFetchStatus by mutableStateOf(CourseFetchStatus.Loading)
    var expandedItemList = mutableStateListOf<Int>()
    var expandedItemListForContent=mutableStateListOf<Int>()
    private val _scrollPosition = MutableStateFlow(0)
    val scrollPosition = _scrollPosition.asStateFlow()
    private val _scrollPositionForContent = MutableStateFlow(0)
    val scrollPositionForContent = _scrollPositionForContent.asStateFlow()

    fun updateScrollPosition(index: Int) {
        _scrollPosition.value = index
    }
    fun updateScrollPositionForContent(index: Int) {
        _scrollPosition.value = index
    }

    fun refreshState() {
        viewModelScope.launch{
            try {
                courseUiState= CourseFetchStatus.Success(
                    topicList = repository.getCourseCheckpoints(title),
                    currentVideoId=repository.fetchYoutubeVideoLink(title)?: repository.saveYoutubeVideoId(title),
                    otherReferences = parseContentList(repository.getSavedReferences(title)?:repository.saveAllReferences(title))
                )
            }catch (e: Exception){
                CourseFetchStatus.Error(e.message.toString())
            }
        }
    }

}
fun parseContentList(input: String): List<Content> {
    return input.split("::")
        .mapNotNull { entry ->
            val regex = Regex("(.+)`(.+)`")
            val matchResult = regex.find(entry.trim())

            matchResult?.let {
                Content(
                    contentName = it.groupValues[1].trim(),
                    contentDescription = it.groupValues[2].trim()
                )
            }
        }
}


data class Content(val contentName: String, val contentDescription: String)


sealed class CourseFetchStatus {
    data class Success(val topicList: List<CourseCheckpoint>, val currentVideoId: String,val otherReferences: List<Content>) : CourseFetchStatus()
    object Loading : CourseFetchStatus()
    data class Error(val error: String) : CourseFetchStatus()
    object Waiting : CourseFetchStatus()
}
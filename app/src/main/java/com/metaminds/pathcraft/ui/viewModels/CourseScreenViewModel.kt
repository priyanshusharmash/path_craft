package com.metaminds.pathcraft.ui.viewModels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metaminds.pathcraft.data.AppRepository
import com.metaminds.pathcraft.ui.screens.courseScreens.CourseScreenNavigationDestination
import kotlinx.coroutines.launch

class CourseScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: AppRepository
): ViewModel() {
    private val encodedTitle:String=checkNotNull(savedStateHandle[CourseScreenNavigationDestination.COURSE])
    val title: String = Uri.decode(encodedTitle)
    var courseUiState: CourseFetchStatus by  mutableStateOf(CourseFetchStatus.Loading)
    init {
        viewModelScope.launch {
            courseUiState = try {
                CourseFetchStatus.Success(
                    topicList = repository.getCourseCheckpoints(title)
                )
            }catch(e:Exception){
                CourseFetchStatus.Error(e.message.toString())
            }
        }

    }
}

sealed class CourseFetchStatus{
    data class Success(val topicList:List<CourseCheckpoint>): CourseFetchStatus()
    object Loading: CourseFetchStatus()
    data class Error(val error:String): CourseFetchStatus()
}
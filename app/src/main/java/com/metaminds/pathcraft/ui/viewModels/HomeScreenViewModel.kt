package com.metaminds.pathcraft.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metaminds.pathcraft.R
import com.metaminds.pathcraft.data.AppRepository
import kotlinx.coroutines.launch
import java.time.LocalTime

class HomeScreenViewModel(private val repository: AppRepository):ViewModel() {

    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set
    val auth=repository.getAuth()


    fun refreshData(){
        auth.currentUser?.reload()

        homeUiState= HomeUiState.Loading
        viewModelScope.launch{
            homeUiState = try {
                HomeUiState.Success(
                    featuredTopics = repository.generateFeaturedTopics(),
                    trendingTopics = repository.generateTopTrendingSkills(),
                    userTopics = repository.getCourseName()
                )
            }catch (e:Exception){
                HomeUiState.Error(error= e.message.toString())
            }
        }
    }

    fun getGreeting(): Int {
        val currentTime = LocalTime.now()
        return when (currentTime.hour) {
            in 5..11 -> R.string.morning_greeting_msg
            in 12..16 -> R.string.afternoon_greeting_msg
            in 17..22 -> R.string.evening_greeting_msg
            else -> R.string.night_greeting_msg
        }
    }

}

sealed interface HomeUiState{
    class Error(val error: String): HomeUiState
    object Loading: HomeUiState
    class Success(val featuredTopics:List<String>,val trendingTopics:List<String>,val userTopics: List<String>): HomeUiState
}
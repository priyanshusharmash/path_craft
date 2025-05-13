package com.metaminds.pathcraft.ui.viewModels

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.Chat
import com.metaminds.pathcraft.data.AppRepository
import com.metaminds.pathcraft.data.MessageModel
import com.metaminds.pathcraft.ui.screens.ChatScreenNavigationDestination
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatScreenViewModel(
    private val repository: AppRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val messageList = repository.history
    var topicList: List<String> = listOf()
    var chatState: ChatStatus = ChatStatus.GREET
    var dataFetchingState: DataFetchingState by mutableStateOf(DataFetchingState.Loading)
    var courseName: String = savedStateHandle[ChatScreenNavigationDestination.COURSE] ?: ""


    var checkpoints by mutableStateOf("")


    init {
        repository.history.clear()
        messageList.clear()
        val user = repository.getAuth().currentUser
        user?.reload()
        viewModelScope.launch {
            greetUser(user?.displayName.toString())
            if(courseName.isNotEmpty()){
                chatState= ChatStatus.GENERATE_ROADMAP
                delay(1000)
                sendMessage(courseName)
            }
        }
    }

    fun sendMessage(prompt: String){
        viewModelScope.launch {
            try {
                courseName=prompt
                if (chatState == ChatStatus.GENERATE_ROADMAP) {
                    generateRoadmap(prompt)
                    dataFetchingState = DataFetchingState.Loading
                    chatState = ChatStatus.GENERATE_SUBTOPICS
                    generateSubTopics()
                    chatState= ChatStatus.End
                }
            }catch (e: Exception){
                dataFetchingState= DataFetchingState.Error(e.message.toString())
                repository.history.add(MessageModel(message=e.message.toString(),role="model"))
            }
        }
    }

    fun onBackPressed(){
        viewModelScope.cancel()
        if(chatState== ChatStatus.End) {
            repository.saveNewCourse(
                courseName = courseName, courseCheckpointList = parseTopics(
                    input = checkpoints
                )
            )
        }
    }

    private suspend fun greetUser(username: String) {
        val response = repository.greetUser(username)
        delay(1000)
        repository.history.add(MessageModel(message = response, role = "model"))
        delay(1000)
        repository.history.add(
            MessageModel(
                message = "What would you like to learn?",
                role = "model"
            )
        )
        chatState = ChatStatus.GENERATE_ROADMAP
        dataFetchingState = DataFetchingState.Success
    }

    private suspend fun generateRoadmap(topic: String) {
        repository.history.add(MessageModel(message = topic, role = "user"))
        dataFetchingState= DataFetchingState.Loading
        delay(1000)
        repository.history.add(
            MessageModel(
                message = "Generating a personalized Roadmap...",
                role = "model"
            )
        )
        val response = repository.generateRoadmap(topic)
        repository.history.add(MessageModel(message = response, role = "model", isShown = false))
        topicList = response.split("::")
        dataFetchingState = DataFetchingState.Success

    }

    private suspend fun generateSubTopics() {
        delay(1000)
        repository.history.add(MessageModel(message = "Generating full map...", role = "model"))
        val response = repository.generateSubTopics()
        checkpoints=response
        repository.history.add(MessageModel(message = response, role = "model"))
        dataFetchingState= DataFetchingState.Success
    }

    private fun parseTopics(input: String): List<CourseCheckpoint> {
        val regexTopic = Regex("_([^_]+)_") // Matches topics enclosed in `_`
        val regexSubTopic = Regex("\\*(.*)") // Matches subtopics starting with bullet point `•`

        val result = mutableListOf<CourseCheckpoint>()
        var currentTopic = ""
        val subTopics = mutableListOf<String>()

        input.lines().forEach { line ->
            val topicMatch = regexTopic.find(line)
            val subTopicMatch = regexSubTopic.find(line)

            if (topicMatch != null) {
                // Save the previous topic and its subtopics before switching to the new topic
                if (currentTopic.isNotEmpty()) {
                    result.add(CourseCheckpoint(checkpoint = currentTopic, subTopics = subTopics.toList()))
                    subTopics.clear() // Reset for the next topic
                }
                currentTopic = topicMatch.groupValues[1] // Extract topic
            } else if (subTopicMatch != null) {
                subTopics.add(subTopicMatch.groupValues[1].trim()) // Extract subtopic
            }
        }

        // Add last topic and its subtopics
        if (currentTopic.isNotEmpty()) {
            result.add(CourseCheckpoint(checkpoint = currentTopic, subTopics = subTopics.toList()))
        }

        return result
    }



    fun formatText(input: String): AnnotatedString {
        return buildAnnotatedString {
            val regex = Regex("_(.+?)_")
            var currentIndex = 0
            regex.findAll(input).forEach { matchResult ->

                val start = matchResult.range.first
                val end = matchResult.range.last
                if (currentIndex < start) {
                    val normalText = input.substring(currentIndex, start).replace("*", "•")
                    append(normalText)
                }

                pushStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                append(matchResult.groupValues[1])
                pop()

                currentIndex = end + 1
            }
            if (currentIndex < input.length) {
                val remainingText = input.substring(currentIndex).replace("*", "•")
                append(remainingText)
            }
        }
    }


}
sealed interface DataFetchingState {
    object Success : DataFetchingState
    object Loading : DataFetchingState
    data class Error(val error: String) : DataFetchingState
}



enum class ChatStatus {
    GREET,
    GENERATE_ROADMAP,
    GENERATE_SUBTOPICS,
    End
}

data class CourseCheckpoint(
    val checkpoint:String,
    var subTopics:List<String>
)


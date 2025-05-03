package com.metaminds.pathcraft.ui.viewModels

import android.provider.ContactsContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.metaminds.pathcraft.data.AppRepository
import com.metaminds.pathcraft.data.MessageModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatScreenViewModel(private val repository: AppRepository) : ViewModel() {


    val messageList = repository.history
    var topicList: List<String> = listOf()
    var chatState: ChatStatus = ChatStatus.GREET
    var dataFetchingState: DataFetchingState by mutableStateOf(DataFetchingState.Loading)


    init {
        val user = repository.getAuth().currentUser
        user?.reload()
        viewModelScope.launch {
            greetUser(user?.displayName.toString())
        }
    }

    fun sendMessage(prompt: String): ChatStatus {
        viewModelScope.launch {
            if (chatState == ChatStatus.GENERATE_ROADMAP) {
                generateRoadmap(prompt)
                dataFetchingState = DataFetchingState.Loading
                chatState = ChatStatus.GENERATE_SUBTOPICS
                generateSubTopics()
            }
        }
        return chatState
    }

    fun getAuth(): FirebaseAuth = repository.getAuth()

    fun clearChatHistory() {
        repository.history.clear()
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
        delay(1000)
        repository.history.add(MessageModel(message = response, role = "model"))
        dataFetchingState= DataFetchingState.Success
    }
}
sealed interface DataFetchingState {
    object Success : DataFetchingState
    object Loading : DataFetchingState
    object Error : DataFetchingState
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

enum class ChatStatus {
    GREET,
    GENERATE_ROADMAP,
    GENERATE_SUBTOPICS
}


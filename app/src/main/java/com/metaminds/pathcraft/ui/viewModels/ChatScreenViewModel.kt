package com.metaminds.pathcraft.ui.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.metaminds.pathcraft.data.AppRepository
import com.metaminds.pathcraft.data.MessageModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatScreenViewModel(private val repository : AppRepository): ViewModel() {


    val messageList = repository.history
    var topicList: List<String> = listOf()
    var chatState : ChatStatus = ChatStatus.GREET

    init {
        viewModelScope.launch {
            val user = repository.getAuth().currentUser
            user?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    greetUser(user.displayName?:"Guest")
                }
            }
        }
    }

    fun sendMessage(prompt: String): ChatStatus{
        viewModelScope.launch {
            if (chatState == ChatStatus.GENERATE_ROADMAP) {
                generateRoadmap(prompt)
                chatState = ChatStatus.GENERATE_SUBTOPICS
                generateSubTopics()
            }
        }
        return chatState
    }

    fun getAuth(): FirebaseAuth = repository.getAuth()

    private fun greetUser(username:String){
        viewModelScope.launch {
            val response = repository.greetUser(username)
            repository.history.add(MessageModel(message = response, role = "model"))
            repository.history.add(MessageModel(message = "What would you like to learn?", role = "model"))
            chatState= ChatStatus.GENERATE_ROADMAP
        }
    }

    private suspend fun generateRoadmap(topic:String){
            repository.history.add(MessageModel(message = topic, role = "user"))
            repository.history.add(MessageModel(message = "Generating a personalized Roadmap...",role = "model"))
            val response = repository.generateRoadmap(topic)
            repository.history.add(MessageModel(message = response, role = "model", isShown = false))
            topicList = response.split("::")

    }
    private suspend fun generateSubTopics(){
        repository.history.add(MessageModel(message = "Generating providing full map...", role = "model"))
            val response= repository.generateSubTopics()
            repository.history.add(MessageModel(message = response, role = "model"))
    }

}

enum class ChatStatus{
    GREET,
    GENERATE_ROADMAP,
    GENERATE_SUBTOPICS
}


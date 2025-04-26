package com.metaminds.pathcraft.data

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel

interface GeminiRepository {
    val generativeModel: GenerativeModel
    val chat: Chat
    val history:MutableList<MessageModel>
    fun greetUser(username:String): String
    suspend fun generateRoadmap(topic: String): String
    suspend fun generateSubTopics(): String
}

data class MessageModel(
    val isShown: Boolean=true,
    val message:String,
    val role: String
)
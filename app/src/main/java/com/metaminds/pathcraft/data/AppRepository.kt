package com.metaminds.pathcraft.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.metaminds.pathcraft.network.UnsplashSearchResponse
import com.metaminds.pathcraft.network.UpslashApiService
import com.metaminds.pathcraft.network.UpslashPhotos
import java.util.Properties

private fun getApiKey(context: Context): String {
    val properties = Properties()
    context.assets.open("apikeys.properties").use { inputStream ->
        properties.load(inputStream)
    }
    return properties.getProperty("API_KEY") ?: throw Exception("API_KEY not found")
}

class AppRepository(private val auth: FirebaseAuth,private val context: Context, private val upslashApiService: UpslashApiService)
    : GeminiRepository, FirebaseRepository, UpshashApiRepository {
    override val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = getApiKey(context)
    )
    override val history: MutableList<MessageModel> = mutableStateListOf()
    override val chat: Chat = generativeModel.startChat(
        history=history.map { messageModel->
            content(role= messageModel.role){messageModel.message}
        }.toList()
    )

    override fun greetUser(username: String):String {
        return "Hi, $username"
    }

    override suspend fun generateRoadmap(topic: String): String {
        val response = chat.sendMessage("I want to learn $topic from scratch," +
                " so generate a roadmap containing the topics or checkpoints. " +
                "Important you must only provide the response in format - " +
                "topic::topic::topic… and so on do not include any other thing strictly."
        )
        return response.text.toString().trim()
    }

    override suspend fun generateSubTopics(): String {
        val response = chat.sendMessage("generate all sub topics of the topics you previously" +
                "generated. Note that this is going to be a for learning from basics so " +
                "you have to include all topics with detail.This time do not separate with ::." +
                "Guidance for your response - The main topic should be  between _ mark and must end with '\n'." +
                "The subtopic should start with a bullet point and must end with '\n' character ." +
                "The last subtopic must end with two \n character" +
                "Warning - Do not write _ or * anywhere else, this is very important" +
                "Strictly follow the guidelines.")
        return response.text.toString()
    }

    override fun getAuth(): FirebaseAuth = auth


    override fun signUpWithEmailPassword(
        email: String,
        password: String,
        callback:(Boolean,String?)-> Unit
    ) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    return@addOnCompleteListener (callback(true,null))
                }else{
                    return@addOnCompleteListener (callback(false,task.exception?.message))
                }
            }
    }

    override fun logInWithEmailPassword(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ){
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    return@addOnCompleteListener (callback(true,null))
                }else{
                    return@addOnCompleteListener (callback(false,task.exception?.message))
                }
            }
    }

    override suspend fun generateFeaturedTopics():List<String> {
        val response = chat.sendMessage("tell me 20 featured topics in the field of computer technology" +
                "Your response should be in the form of topic::topic:: and so on." +
                "Warning- do not write anything else in your response")
        return response.text.toString().split("::")
    }

    override suspend fun generateTopTrendingSkills(): List<String> {
        val response=chat.sendMessage("tell me 20 top trending skills in the field of computer technology" +
                "and development." +
                "Your response should be in the form of topic::topic:: and so on." +
                "Warning- do not write anything else in your response")
        return response.text.toString().split("::")
    }

    override suspend fun getUpshashPhoto(query: String): UnsplashSearchResponse = upslashApiService.searchPhotos(query)
}
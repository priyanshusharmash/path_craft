package com.metaminds.pathcraft.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import java.util.Properties

private fun getApiKey(context: Context): String {
    val properties = Properties()
    context.assets.open("apikeys.properties").use { inputStream ->
        properties.load(inputStream)
    }
    return properties.getProperty("API_KEY") ?: throw Exception("API_KEY not found")
}

class AppRepository(private val auth: FirebaseAuth,private val context: Context)
    : GeminiRepository, FirebaseRepository {
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
                "you have to include all topics with detail")
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
}
package com.metaminds.pathcraft.data

import android.content.Context
import androidx.compose.animation.core.snap
import androidx.compose.runtime.mutableStateListOf
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.metaminds.pathcraft.network.UnsplashSearchResponse
import com.metaminds.pathcraft.network.UpslashApiService
import com.metaminds.pathcraft.network.YoutubeApiService
import com.metaminds.pathcraft.ui.viewModels.CourseCheckpoint
import kotlinx.coroutines.tasks.await
import java.util.Properties

private fun getGeminiApiKey(context: Context): String {
    val properties = Properties()
    context.assets.open("apikeys.properties").use { inputStream ->
        properties.load(inputStream)
    }
    return properties.getProperty("API_KEY") ?: throw Exception("API_KEY not found")
}
private fun getYoutubeApiKey1(context: Context): String {
    val properties = Properties()
    context.assets.open("apikeys.properties").use { inputStream ->
        properties.load(inputStream)
    }
    return properties.getProperty("YOUTUBE_API_KEY[1]") ?: throw Exception("API_KEY not found")
}
private fun getYoutubeApiKey2(context: Context): String {
    val properties = Properties()
    context.assets.open("apikeys.properties").use { inputStream ->
        properties.load(inputStream)
    }
    return properties.getProperty("YOUTUBE_API_KEY[2]") ?: throw Exception("API_KEY not found")
}
private fun getUnsplashApiKey(context: Context): String {
    val properties = Properties()
    context.assets.open("apikeys.properties").use { inputStream ->
        properties.load(inputStream)
    }
    return properties.getProperty("UNSPLASH_API_KEY") ?: throw Exception("API_KEY not found")
}

class AppRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val context: Context,
    private val upslashApiService: UpslashApiService,
    private val youtubeApiService: YoutubeApiService
) : GeminiRepository, FirebaseRepository, UpshashApiRepository {
    override val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = getGeminiApiKey(context)
    )
    override val history: MutableList<MessageModel> = mutableStateListOf()
    override val chat: Chat = generativeModel.startChat(
        history = history.map { messageModel ->
            content(role = messageModel.role) { messageModel.message }
        }.toList()
    )

    override fun greetUser(username: String): String {
        return "Hi, $username"
    }

    override suspend fun generateRoadmap(topic: String): String {
        val response = chat.sendMessage(
            "I want to learn $topic from scratch," +
                    " so generate a roadmap containing the topics or checkpoints. " +
                    "Important you must only provide the response in format - " +
                    "topic::topic::topic… and so on do not include any other thing strictly."
        )
        return response.text.toString().trim()
    }

    override suspend fun generateSubTopics(): String {
        val response = chat.sendMessage(
            "generate all sub topics of the topics you previously" +
                    "generated. Note that this is going to be a for learning from basics so " +
                    "you have to include all topics with detail.This time do not separate with ::." +
                    "Guidance for your response - The main topic should be  between _ mark and must end with '\n'." +
                    "The subtopic should start with a bullet point and must end with '\n' character ." +
                    "The last subtopic must end with two \n character" +
                    "Warning - Do not write _ or * anywhere else, this is very important" +
                    "Strictly follow the guidelines."
        )
        return response.text.toString()
    }

    override fun getAuth(): FirebaseAuth = auth


    override fun signUpWithEmailPassword(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    return@addOnCompleteListener (callback(true, null))
                } else {
                    return@addOnCompleteListener (callback(false, task.exception?.message))
                }
            }
    }

    override fun logInWithEmailPassword(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    return@addOnCompleteListener (callback(true, null))
                } else {
                    return@addOnCompleteListener (callback(false, task.exception?.message))
                }
            }
    }

    override suspend fun generateFeaturedTopics(): List<String> {
        val response = chat.sendMessage(
            "tell me 20 featured topics in the field of computer technology" +
                    "Your response should be in the form of topic::topic:: and so on." +
                    "Warning- do not write anything else in your response"
        )
        return response.text.toString().split("::")
    }

    override suspend fun generateTopTrendingSkills(): List<String> {
        val response = chat.sendMessage(
            "tell me 20 top trending skills in the field of computer technology" +
                    "and development." +
                    "Your response should be in the form of topic::topic:: and so on." +
                    "Warning- do not write anything else in your response"
        )
        return response.text.toString().split("::")
    }

    override suspend fun generateTopicContent(topic:String,subTopic: String): String{
        val response=chat.sendMessage(
            "I want to learn $subTopic in respect to $topic " +
                    "so teach me $subTopic " +
                    "instructions- i am beginner, generate only content not any other thing other than the content, " +
                    "teach me in a book language and as a professional master of this topic ," +
                    "do not display any table"
        )
        return response.text.toString()
    }

    override suspend fun getContentNotes(courseName: String, topic: String, subTopic: String): String? {
        val documentSnapshot = firestore
            .collection("users")
            .document(auth.currentUser!!.uid)
            .collection("learning")
            .document(courseName.replace('/','_'))
            .collection(topic.replace('/','_'))
            .document(subTopic.replace('/','_'))
            .get()
            .await()
        return documentSnapshot.getString("content")
    }

    override suspend fun saveContentNotes(courseName:String,topic: String,subTopic: String,content: String) {
        val data = mapOf(
            "content" to content
        )
        firestore.collection("users").document(auth.currentUser!!.uid).collection("learning")
            .document(courseName.replace('/','_'))
            .collection(topic.replace('/','_'))
            .document(subTopic.replace('/','_'))
            .set(data)

    }

    override suspend fun getUpshashPhoto(query: String): UnsplashSearchResponse =
        upslashApiService.searchPhotos("$query in technology", clientId = getUnsplashApiKey(context))

    override fun saveNewCourse(
        courseName: String,
        courseCheckpointList: List<CourseCheckpoint>
    ) {
        val formattedCheckpoints = courseCheckpointList.map { checkpoint ->
            mapOf(
                "checkpoint" to checkpoint.checkpoint,
                "subTopics" to checkpoint.subTopics
            )
        }
        val course = hashMapOf(
            "name" to courseName,
            "courseCheckpointList" to formattedCheckpoints
        )
        firestore.collection("users").document(auth.currentUser!!.uid).collection("learning")
            .document(courseName.replace('/','_'))
            .set(course)
    }

    suspend fun getCourseCheckpoints(courseName: String): List<CourseCheckpoint> {
        val documentSnapshot = firestore.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("learning")
            .document(courseName.replace('/','_'))
            .get()
            .await()
        if (!documentSnapshot.exists()) {
            return emptyList()
        }
        val rawCheckpoints = documentSnapshot.get("courseCheckpointList") as? List<*> ?: emptyList<String>()

        return rawCheckpoints.mapNotNull { item ->
            (item as? Map<*, *>)?.let { checkpoint ->
                val name = checkpoint["checkpoint"] as? String
                val subTopics = (checkpoint["subTopics"] as? List<*>)?.filterIsInstance<String>()
                if (name != null && subTopics != null) {
                    CourseCheckpoint(checkpoint = name, subTopics = subTopics)
                } else {
                    null
                }
            }
        }
    }

    override suspend fun getCourseName(): List<String> {
        return try {
            val querySnapshot = firestore.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("learning")
                .get()
                .await()
            querySnapshot.documents.mapNotNull { it.getString("name") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun getYoutubeVideoLinks(query: String): String{
        val searchResponse = youtubeApiService.searchVideos(query = "full $query in one shot from beginner to advanced", maxResults = 5, apiKey =getYoutubeApiKey1(context))
        val videoIds = searchResponse.items.mapNotNull { it.id.videoId }
        val statusResponse = youtubeApiService.getVideoDetails(
            ids = videoIds.joinToString(","),
            apiKey =getYoutubeApiKey2(context)
        )
        val publicAndEmbeddableIds = statusResponse.items.filter {
            it.status.privacyStatus == "public" && it.status.embeddable
        }.map { it.id }
        return publicAndEmbeddableIds[0]

    }

    suspend fun saveYoutubeVideoId(courseName:String): String{
        val id = getYoutubeVideoLinks(courseName)
        firestore.collection("users").document(auth.currentUser!!.uid).collection("learning")
            .document(courseName.replace('/','_'))
            .update(mapOf(
                "youtubeVideoId" to id
            ))
        return id
    }

    suspend fun fetchYoutubeVideoLink(courseName: String): String?{
        val documentSnapshot = firestore.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("learning")
            .document(courseName.replace('/','_'))
            .get()
            .await()
        return if(documentSnapshot.contains("youtubeVideoId"))documentSnapshot.get("youtubeVideoId").toString() else null
    }

    private suspend fun getAllReferences(courseName: String): String{
        val response = chat.sendMessage("Get all content reference available on internet for learning $courseName" +
                "instructions-> provide the content in format - ContentName`content description/information`::ContentName 'content description/information` and so on" +
                "do not include or write any other word in your response" +
                "do not include any youtube content" +
                "you can include references of online courses on coursera or udemy, any online free ebook and so on")
        return response.text.toString()
    }

    suspend fun saveAllReferences(courseName:String): String{
        val allReferences=getAllReferences(courseName)
        firestore.collection("users").document(auth.currentUser!!.uid).collection("learning")
            .document(courseName.replace('/','_'))
            .update(mapOf(
                "otherResources" to allReferences
            ))
        return allReferences
    }

    suspend fun getSavedReferences(courseName:String): String?{
        val snapshot = firestore.collection("users").document(auth.currentUser!!.uid).collection("learning")
            .document(courseName.replace('/','_'))
            .get()
            .await()
        return if(snapshot.contains("otherResources")) snapshot.get("otherResources").toString() else null
    }

    suspend fun saveUnSplashImageUrl(query: String): String{
        val url=getUpshashPhoto(query).results[0].urls.regular
        firestore.collection("users").document(auth.currentUser!!.uid).collection("learning")
            .document(query.replace('/','_'))
            .update(mapOf(
                "image" to url
            ))
        return url
    }

    suspend fun getSavedCourseImage(courseName:String): String?{
        val snapshot=firestore.collection("users").document(auth.currentUser!!.uid).collection("learning")
            .document(courseName.replace('/','_'))
            .get()
            .await()
        return if(snapshot.contains("image")) snapshot.get("image").toString() else null
    }


}
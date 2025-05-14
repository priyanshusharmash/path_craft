package com.metaminds.pathcraft.data

import android.R
import com.google.firebase.auth.FirebaseAuth
import com.metaminds.pathcraft.ui.viewModels.CourseCheckpoint


interface FirebaseRepository {
    fun getAuth(): FirebaseAuth
    fun signUpWithEmailPassword(email:String,password: String,callback:(Boolean,String?)-> Unit)
    fun logInWithEmailPassword(email:String,password:String,callback: (Boolean, String?) -> Unit)
    fun saveNewCourse(courseName:String,courseCheckpointList: List<CourseCheckpoint>)
    suspend fun getCourseName():List<String>
    suspend fun saveContentNotes(courseName:String,topic: String,subTopic: String,content: String)
    suspend fun getContentNotes(courseName: String, topic: String, subTopic: String): String?
}
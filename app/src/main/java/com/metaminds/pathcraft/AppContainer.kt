package com.metaminds.pathcraft

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.metaminds.pathcraft.data.AppRepository
import com.metaminds.pathcraft.network.UpslashApiService
import com.metaminds.pathcraft.network.YoutubeApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer{
    val repository: AppRepository
    val auth : FirebaseAuth
    val firestore: FirebaseFirestore
}

class DefaultApplication(context: Context): AppContainer{
    override val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override val firestore: FirebaseFirestore = Firebase.firestore
    private val upshashBaseUrl: String ="https://api.unsplash.com/"
    private val unSplashRetrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(upshashBaseUrl)
        .build()

    private val upslashApiService: UpslashApiService by lazy {
        unSplashRetrofit.create(UpslashApiService::class.java)
    }



    private val youtubeApiBaseUrl:String="https://www.googleapis.com/youtube/v3/"
    private val json =Json {
        ignoreUnknownKeys=true
    }
    private val youtubeRetrofit= Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(youtubeApiBaseUrl)
        .build()

    private val youtubeApiService: YoutubeApiService by lazy {
        youtubeRetrofit.create(YoutubeApiService::class.java)
    }

    override val repository: AppRepository = AppRepository(
        auth= auth,
        context=context,
        upslashApiService = upslashApiService,
        firestore = firestore,
        youtubeApiService = youtubeApiService
    )
}
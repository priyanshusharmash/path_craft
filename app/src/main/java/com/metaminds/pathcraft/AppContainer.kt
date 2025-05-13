package com.metaminds.pathcraft

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.metaminds.pathcraft.data.AppRepository
import com.metaminds.pathcraft.network.UpslashApiService
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
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(upshashBaseUrl)
        .build()

    private val upslashApiService: UpslashApiService by lazy {
        retrofit.create(UpslashApiService::class.java)
    }

    override val repository: AppRepository = AppRepository(
        auth= auth,
        context=context,
        upslashApiService = upslashApiService,
        firestore = firestore
    )
}
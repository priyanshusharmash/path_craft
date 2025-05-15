package com.metaminds.pathcraft.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import retrofit2.http.GET
import retrofit2.http.Query

interface UpslashApiService {
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query")query:String,
        @Query("per_page") perPage: Int = 1,
        @Query("client_id")clientId: String
    ): UnsplashSearchResponse

}

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class UnsplashSearchResponse(
    val results: List<UpslashPhotos>
)

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class UpslashPhotos(
    val id:String,
    val urls:Urls
)



@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Urls(
    val regular: String
)
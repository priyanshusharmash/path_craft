package com.metaminds.pathcraft.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApiService{
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part:String ="snippet",
        @Query("q") query: String,
        @Query("type") type:String = "video",
        @Query("key") apiKey:String,
        @Query("maxResults") maxResults:Int = 10
    ): YoutubeResponse
    @GET("videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "status",
        @Query("id") ids: String, // comma-separated video IDs
        @Query("key") apiKey: String
    ): YouTubeVideosStatusResponse

}


@Serializable
data class YoutubeResponse(
    val items: List<VideoItem>
)

@Serializable
data class VideoItem(
    val id: VideoId
)

@Serializable
data class VideoId(
    val kind: String,
    @SerialName("videoId") val videoId:String?=null
)






@Serializable
data class YouTubeVideosStatusResponse(
    val items: List<VideoStatusItem>
)

@Serializable
data class VideoStatusItem(
    val id: String,
    val status: VideoStatus
)

@Serializable
data class VideoStatus(
    val uploadStatus: String,
    val privacyStatus: String,
    val embeddable: Boolean
)

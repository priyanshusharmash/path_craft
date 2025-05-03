package com.metaminds.pathcraft.data

import com.metaminds.pathcraft.network.UnsplashSearchResponse

interface UpshashApiRepository {
    suspend fun getUpshashPhoto(query:String): UnsplashSearchResponse
}
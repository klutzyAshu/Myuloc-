package com.example.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InvidiousApi {
    @GET("api/v1/search")
    suspend fun searchVideos(
        @Query("q") query: String,
        @Query("type") type: String = "video"
    ): List<InvidiousSearchResult>

    @GET("api/v1/videos/{videoId}")
    suspend fun getVideoDetails(
        @Path("videoId") videoId: String
    ): InvidiousVideoDetails
}

data class InvidiousSearchResult(
    val title: String?,
    val videoId: String?,
    val author: String?,
    val lengthSeconds: Long?,
    val videoThumbnails: List<InvidiousThumbnail>?
)

data class InvidiousThumbnail(
    val quality: String?,
    val url: String?,
    val width: Int?,
    val height: Int?
)

data class InvidiousVideoDetails(
    val title: String?,
    val videoId: String?,
    val author: String?,
    val duration: Long?,
    val adaptiveFormats: List<InvidiousAdaptiveFormat>?
)

data class InvidiousAdaptiveFormat(
    val type: String?, // e.g. "audio/webm; codecs=\"opus\""
    val url: String?,
    val container: String?,
    val encoding: String?,
    val bitrate: String?
)

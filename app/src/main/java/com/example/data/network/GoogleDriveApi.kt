package com.example.data.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GoogleDriveApi {
    @GET("drive/v3/files")
    suspend fun listFiles(
        @Header("Authorization") authHeader: String,
        @Query("q") query: String,
        @Query("fields") fields: String = "files(id, name, mimeType, size, thumbnailLink)",
        @Query("supportsAllDrives") supportsAllDrives: Boolean = true,
        @Query("includeItemsFromAllDrives") includeItemsFromAllDrives: Boolean = true
    ): GoogleDriveResponse

    @GET("drive/v3/files")
    suspend fun listPublicFiles(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("fields") fields: String = "files(id, name, mimeType, size, thumbnailLink)",
        @Query("supportsAllDrives") supportsAllDrives: Boolean = true,
        @Query("includeItemsFromAllDrives") includeItemsFromAllDrives: Boolean = true
    ): GoogleDriveResponse

    @GET("drive/v3/about")
    suspend fun getAbout(
        @Header("Authorization") authHeader: String,
        @Query("fields") fields: String = "storageQuota"
    ): GoogleDriveAboutResponse
}

data class GoogleDriveResponse(
    val files: List<GoogleDriveFile>
)

data class GoogleDriveFile(
    val id: String,
    val name: String,
    val mimeType: String,
    val size: String?,
    val thumbnailLink: String?
)

data class GoogleDriveAboutResponse(
    val storageQuota: StorageQuota?
)

data class StorageQuota(
    val limit: String?,
    val usage: String?,
    val usageInDrive: String?,
    val usageInDriveTrash: String?
)

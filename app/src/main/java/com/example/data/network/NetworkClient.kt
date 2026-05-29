package com.example.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    // Google Drive APi Base Retrofit
    val googleDriveApi: GoogleDriveApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GoogleDriveApi::class.java)
    }

    // Invidious API Base Retrofit (configurable, default yewtu.be)
    fun getInvidiousApi(baseUrl: String = "https://yewtu.be/"): InvidiousApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(InvidiousApi::class.java)
    }

    // Official YouTube Data API Search query helper
    fun queryYouTubeDataApi(query: String, apiKey: String): List<InvidiousSearchResult> {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        return try {
            val url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + 
                    java.net.URLEncoder.encode(query, "UTF-8") + 
                    "&type=video&key=" + apiKey + 
                    "&maxResults=15"
            val request = okhttp3.Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    android.util.Log.e("NetworkClient", "Google YT Data API query failed with code: ${response.code}")
                    return emptyList()
                }
                val bodyStr = response.body?.string() ?: return emptyList()
                val jsonObj = org.json.JSONObject(bodyStr)
                val items = jsonObj.optJSONArray("items") ?: return emptyList()
                val results = mutableListOf<InvidiousSearchResult>()
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val idObj = item.optJSONObject("id") ?: continue
                    val videoId = idObj.optString("videoId") ?: ""
                    if (videoId.isEmpty()) continue
                    
                    val snippet = item.optJSONObject("snippet") ?: continue
                    val title = snippet.optString("title") ?: "Unknown Title"
                    val author = snippet.optString("channelTitle") ?: "Unknown Artist"
                    
                    val thumbnails = snippet.optJSONObject("thumbnails")
                    val mediumThumb = thumbnails?.optJSONObject("medium") ?: thumbnails?.optJSONObject("default")
                    val thumbnailUrl = mediumThumb?.optString("url") ?: "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
                    
                    results.add(
                        InvidiousSearchResult(
                            title = title,
                            videoId = videoId,
                            author = author,
                            lengthSeconds = 240L,
                            videoThumbnails = listOf(InvidiousThumbnail(quality = "default", url = thumbnailUrl, width = 320, height = 180))
                        )
                    )
                }
                results
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Direct YouTube Search Page Scraper Helper
    // Returns a list of InvidiousSearchResult parsed from YouTube's results page HTML.
    // Extremely robust as a layout fallback to Invidious searching!
    fun scrapeYouTubeSearch(query: String): List<InvidiousSearchResult> {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        val url = "https://www.youtube.com/results?search_query=" + java.net.URLEncoder.encode(query, "UTF-8")
        val request = okhttp3.Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val html = response.body?.string() ?: return emptyList()
                
                // Parse ytInitialData block from JS
                val targetKey = "var ytInitialData = "
                val startIndex = html.indexOf(targetKey)
                if (startIndex == -1) return emptyList()
                
                val jsonStart = startIndex + targetKey.length
                var jsonEnd = html.indexOf(";</script>", jsonStart)
                if (jsonEnd == -1) {
                    jsonEnd = html.indexOf("};", jsonStart) + 1
                }
                if (jsonEnd == -1 || jsonEnd <= jsonStart) return emptyList()
                
                val rawJson = html.substring(jsonStart, jsonEnd).trim()
                parseYouTubeJsonString(rawJson)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseYouTubeJsonString(json: String): List<InvidiousSearchResult> {
        // Since we can do dynamic extraction, a regex-based light JSON parser is safer 
        // than parsing a massive nested YouTube layout JSON, avoiding ClassNotFound or JsonReader halts.
        val results = mutableListOf<InvidiousSearchResult>()
        
        // Find videoId and title pairings
        val videoIdRegex = """"videoId":"([^"]+)"""".toRegex()
        val titleRegex = """"title":\{"runs":\[\{"text":"([^"]+)"\}\]""".toRegex()
        val ownerRegex = """"ownerText":\{"runs":\[\{"text":"([^"]+)"\}\]""".toRegex()
        val durationRegex = """"lengthText":\{"accessibility":\{"accessibilityData":\{"label":"([^"]+)"\}\},"simpleText":"([^"]+)"\}""".toRegex()

        val videoIds = videoIdRegex.findAll(json).map { it.groupValues[1] }.toList()
        val titles = titleRegex.findAll(json).map { it.groupValues[1] }.toList()
        val owners = ownerRegex.findAll(json).map { it.groupValues[1] }.toList()
        
        val minSize = minOf(videoIds.size, titles.size)
        for (i in 0 until minSize) {
            val vId = videoIds[i]
            val title = titles[i].replace("\\u0026", "&")
            val author = if (i < owners.size) owners[i].replace("\\u0026", "&") else "YouTube"
            
            // Avoid duplicate videos
            if (results.none { it.videoId == vId }) {
                results.add(
                    InvidiousSearchResult(
                        title = title,
                        videoId = vId,
                        author = author,
                        lengthSeconds = 240L, // dynamic placeholder
                        videoThumbnails = listOf(InvidiousThumbnail(quality = "default", url = "https://img.youtube.com/vi/$vId/hqdefault.jpg", width = 120, height = 90))
                    )
                )
            }
            if (results.size >= 12) break
        }
        return results
    }
}

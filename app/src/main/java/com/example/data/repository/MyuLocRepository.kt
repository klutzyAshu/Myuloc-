package com.example.data.repository

import com.example.data.database.CachedTrack
import com.example.data.database.CachedTrackDao
import com.example.data.database.FavoriteTrack
import com.example.data.database.FavoriteTrackDao
import com.example.data.database.MyuLocDatabase
import com.example.data.database.PreferenceDao
import com.example.data.database.PreferenceEntity
import com.example.data.database.OfflineTrack
import kotlinx.coroutines.flow.Flow

class MyuLocRepository(private val database: MyuLocDatabase) {
    private val cachedTrackDao: CachedTrackDao = database.cachedTrackDao()
    private val favoriteTrackDao: FavoriteTrackDao = database.favoriteTrackDao()
    private val preferenceDao: PreferenceDao = database.preferenceDao()
    private val offlineTrackDao = database.offlineTrackDao()

    // Cached Tracks Flow
    val allCachedTracks: Flow<List<CachedTrack>> = cachedTrackDao.getAllCachedTracks()

    suspend fun saveCachedTracks(tracks: List<CachedTrack>) {
        cachedTrackDao.clearAll()
        cachedTrackDao.insertAll(tracks)
    }

    suspend fun clearCachedTracks() {
        cachedTrackDao.clearAll()
    }

    // Offline / Local Tracks Flow
    val offlineTracks: Flow<List<OfflineTrack>> = offlineTrackDao.getAllOfflineTracks()

    suspend fun addOfflineTrack(track: OfflineTrack) {
        offlineTrackDao.insertOfflineTrack(track)
    }

    suspend fun removeOfflineTrack(trackId: String) {
        offlineTrackDao.removeOfflineTrack(trackId)
    }

    suspend fun isOffline(trackId: String): Boolean {
        return offlineTrackDao.isOffline(trackId)
    }

    suspend fun getOfflineTrack(trackId: String): OfflineTrack? {
        return offlineTrackDao.getOfflineTrack(trackId)
    }

    // Favorites Flow
    val favorites: Flow<List<FavoriteTrack>> = favoriteTrackDao.getAllFavorites()

    suspend fun addFavorite(track: FavoriteTrack) {
        favoriteTrackDao.insertFavorite(track)
    }

    suspend fun removeFavorite(trackId: String) {
        favoriteTrackDao.removeFavorite(trackId)
    }

    suspend fun isFavorite(trackId: String): Boolean {
        return favoriteTrackDao.isFavorite(trackId)
    }

    // Preferences Helper
    suspend fun getPreference(key: String): String? {
        return preferenceDao.getPreference(key)
    }

    suspend fun setPreference(key: String, value: String) {
        preferenceDao.setPreference(PreferenceEntity(key, value))
    }

    suspend fun removePreference(key: String) {
        preferenceDao.removePreference(key)
    }

    // Google Drive specifics
    suspend fun getGoogleAccessToken(): String? = getPreference("access_token")
    suspend fun setGoogleAccessToken(token: String) = setPreference("access_token", token)

    suspend fun getGoogleRefreshToken(): String? = getPreference("refresh_token")
    suspend fun setGoogleRefreshToken(token: String) = setPreference("refresh_token", token)

    suspend fun getGoogleExpiresAt(): Long = getPreference("expires_at")?.toLongOrNull() ?: 0L
    suspend fun setGoogleExpiresAt(timeMs: Long) = setPreference("expires_at", timeMs.toString())

    suspend fun getGoogleFolderId(): String = getPreference("folder_id") ?: "root"
    suspend fun setGoogleFolderId(folderId: String) = setPreference("folder_id", folderId)

    suspend fun getThemeMode(): String = getPreference("theme_mode") ?: "dark" // default to dark per warm design
    suspend fun setThemeMode(mode: String) = setPreference("theme_mode", mode)
}

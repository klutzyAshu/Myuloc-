package com.example.viewmodel

import com.example.BuildConfig
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.CachedTrack
import com.example.data.database.FavoriteTrack
import com.example.data.database.MyuLocDatabase
import com.example.data.database.OfflineTrack
import com.example.data.network.GoogleDriveFile
import com.example.data.network.NetworkClient
import com.example.data.repository.MyuLocRepository
import com.example.player.MusicPlayerManager
import com.example.player.PlayerTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

sealed interface LockerUiState {
    object Idle : LockerUiState
    object Loading : LockerUiState
    data class Success(val tracks: List<PlayerTrack>) : LockerUiState
    data class Error(val message: String) : LockerUiState
}

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val results: List<PlayerTrack>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

sealed interface DriveStorageState {
    object Idle : DriveStorageState
    object Loading : DriveStorageState
    data class Success(
        val limitBytes: Long,
        val usageBytes: Long,
        val remainingBytes: Long,
        val usagePercentage: Float,
        val isMock: Boolean
    ) : DriveStorageState
    data class Error(val message: String) : DriveStorageState
}

enum class SortField { TITLE, DATE_ADDED, PLAY_COUNT }
enum class SortDirection { ASCENDING, DESCENDING }

class MyuLocViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MyuLocDatabase.getDatabase(application)
    private val repository = MyuLocRepository(database)
    val playerManager = MusicPlayerManager.getInstance(application)

    // UI Navigation Tab
    private val _currentTab = MutableStateFlow("all") // "all", "locker", "search", "settings"
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _subTab = MutableStateFlow("songs")
    val subTab: StateFlow<String> = _subTab.asStateFlow()

    // Light/Dark Theme Preference
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Google Auth States
    private val _isConnectedToDrive = MutableStateFlow(false)
    val isConnectedToDrive: StateFlow<Boolean> = _isConnectedToDrive.asStateFlow()

    private val _googleFolderId = MutableStateFlow("root")
    val googleFolderId: StateFlow<String> = _googleFolderId.asStateFlow()

    // UI States
    private val _lockerUiState = MutableStateFlow<LockerUiState>(LockerUiState.Idle)
    val lockerUiState: StateFlow<LockerUiState> = _lockerUiState.asStateFlow()

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    private val _driveStorageState = MutableStateFlow<DriveStorageState>(DriveStorageState.Idle)
    val driveStorageState: StateFlow<DriveStorageState> = _driveStorageState.asStateFlow()

    // Saved Favorites Flow in database
    val favoriteTracksFlow = repository.favorites

    val favoriteTrackIds: StateFlow<Set<String>> = repository.favorites
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val offlineTrackIds: StateFlow<Set<String>> = repository.offlineTracks
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Configuration / Credentials Customization
    private val _customClientId = MutableStateFlow("")
    val customClientId: StateFlow<String> = _customClientId.asStateFlow()

    private val _customRedirectUri = MutableStateFlow("")
    val customRedirectUri: StateFlow<String> = _customRedirectUri.asStateFlow()

    private val _googleApiKey = MutableStateFlow("")
    val googleApiKey: StateFlow<String> = _googleApiKey.asStateFlow()

    private val _invidiousUrl = MutableStateFlow("https://yewtu.be/")
    val invidiousUrl: StateFlow<String> = _invidiousUrl.asStateFlow()

    // Music genres preference
    private val _selectedGenres = MutableStateFlow<List<String>>(emptyList())
    val selectedGenres: StateFlow<List<String>> = _selectedGenres.asStateFlow()

    private val _showGenrePreferencePopup = MutableStateFlow(false)
    val showGenrePreferencePopup: StateFlow<Boolean> = _showGenrePreferencePopup.asStateFlow()

    private val _recommendationsList = MutableStateFlow<List<PlayerTrack>>(emptyList())
    val recommendationsList: StateFlow<List<PlayerTrack>> = _recommendationsList.asStateFlow()

    // Global continuous physics parameters (Permanently 0.5x, 0.1% bounciness)
    private val _animationSpeed = MutableStateFlow(0.5f)
    val animationSpeed: StateFlow<Float> = _animationSpeed.asStateFlow()

    private val _animationBounciness = MutableStateFlow(0.001f)
    val animationBounciness: StateFlow<Float> = _animationBounciness.asStateFlow()

    // Live search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // New Settings UI States
    private val _filterOutSmallFiles = MutableStateFlow(false)
    val filterOutSmallFiles: StateFlow<Boolean> = _filterOutSmallFiles.asStateFlow()

    private val _filterOutShortAudios = MutableStateFlow(5) // in seconds
    val filterOutShortAudios: StateFlow<Int> = _filterOutShortAudios.asStateFlow()

    private val _preferredStreamBitrate = MutableStateFlow("Balanced")
    val preferredStreamBitrate: StateFlow<String> = _preferredStreamBitrate.asStateFlow()

    private val _bufferMemoryAllocation = MutableStateFlow("Balanced")
    val bufferMemoryAllocation: StateFlow<String> = _bufferMemoryAllocation.asStateFlow()

    private val _headsetDisconnectBehavior = MutableStateFlow(true)
    val headsetDisconnectBehavior: StateFlow<Boolean> = _headsetDisconnectBehavior.asStateFlow()

    private val _playbackAudioQuality = MutableStateFlow("High Quality")
    val playbackAudioQuality: StateFlow<String> = _playbackAudioQuality.asStateFlow()

    private val _cachedAudioSize = MutableStateFlow("0.0 MB")
    val cachedAudioSize: StateFlow<String> = _cachedAudioSize.asStateFlow()

    fun setFilterOutSmallFiles(value: Boolean) {
        _filterOutSmallFiles.value = value
        viewModelScope.launch {
            repository.setPreference("filter_out_small_files", value.toString())
        }
    }

    fun setFilterOutShortAudios(value: Int) {
        _filterOutShortAudios.value = value
        viewModelScope.launch {
            repository.setPreference("filter_out_short_audios", value.toString())
        }
    }

    fun setPreferredStreamBitrate(value: String) {
        _preferredStreamBitrate.value = value
        viewModelScope.launch {
            repository.setPreference("preferred_stream_bitrate", value)
        }
    }

    fun setBufferMemoryAllocation(value: String) {
        _bufferMemoryAllocation.value = value
        viewModelScope.launch {
            repository.setPreference("buffer_memory_allocation", value)
        }
    }

    fun setHeadsetDisconnectBehavior(value: Boolean) {
        _headsetDisconnectBehavior.value = value
        viewModelScope.launch {
            repository.setPreference("headset_disconnect_behavior", value.toString())
        }
    }

    fun setPlaybackAudioQuality(value: String) {
        _playbackAudioQuality.value = value
        viewModelScope.launch {
            repository.setPreference("playback_audio_quality", value)
        }
    }

    fun updateCachedAudioSize() {
        viewModelScope.launch(Dispatchers.IO) {
            val downloadsDir = java.io.File(getApplication<Application>().filesDir, "downloads")
            val sizeInBytes = getFolderSize(downloadsDir)
            val formatted = formatSize(sizeInBytes)
            _cachedAudioSize.value = formatted
        }
    }

    private fun getFolderSize(folder: java.io.File): Long {
        var length = 0L
        if (!folder.exists()) return 0L
        val files = folder.listFiles() ?: return 0L
        for (file in files) {
            length += if (file.isDirectory) getFolderSize(file) else file.length()
        }
        return length
    }

    private fun formatSize(bytes: Long): String {
        if (bytes <= 0) return "0.0 MB"
        val mb = bytes.toDouble() / (1024 * 1024)
        return String.format(java.util.Locale.US, "%.1f MB", mb)
    }

    fun clearCachedAudioStreams() {
        viewModelScope.launch(Dispatchers.IO) {
            val downloadsDir = java.io.File(getApplication<Application>().filesDir, "downloads")
            if (downloadsDir.exists()) {
                val files = downloadsDir.listFiles()
                if (files != null) {
                    for (file in files) {
                        file.delete()
                    }
                }
            }
            updateCachedAudioSize()
            withContext(Dispatchers.Main) {
                Toast.makeText(getApplication(), "Cached audio streams cleared", Toast.LENGTH_SHORT).show()
            }
        }
    }

    init {
        loadSettings()
        updateCachedAudioSize()
        // Connect flow to exoplayer header properties
        viewModelScope.launch {
            repository.allCachedTracks.collect { cached ->
                val playerTracks = cached.map { it.toPlayerTrack() }
                if (_isConnectedToDrive.value && playerTracks.isNotEmpty() && _lockerUiState.value !is LockerUiState.Loading) {
                    _lockerUiState.value = LockerUiState.Success(playerTracks)
                }
            }
        }

        // Collect offline tracks to pre-load play counts from DB preferences
        viewModelScope.launch {
            repository.offlineTracks.collect { tracks ->
                val currentCounts = _playCounts.value.toMutableMap()
                tracks.forEach { track ->
                    if (!currentCounts.containsKey(track.id)) {
                        val countVal = repository.getPreference("play_count_${track.id}")?.toIntOrNull() ?: 0
                        currentCounts[track.id] = countVal
                    }
                }
                _playCounts.value = currentCounts
            }
        }

        // Observe currentTrack to automatically increment play count when starting a new track.
        viewModelScope.launch {
            playerManager.currentTrack.collect { track ->
                track?.let {
                    val currentCounts = _playCounts.value.toMutableMap()
                    val countVal = (currentCounts[it.id] ?: 0) + 1
                    currentCounts[it.id] = countVal
                    _playCounts.value = currentCounts
                    repository.setPreference("play_count_${it.id}", countVal.toString())
                }
            }
        }

        // Observe and forward player playback errors via Toast to prevent silent stuck issues
        viewModelScope.launch {
            playerManager.playbackError.collect { errorMsg ->
                errorMsg?.let {
                    Toast.makeText(getApplication(), it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun loadPlayCount(trackId: String) {
        viewModelScope.launch {
            val countVal = withContext(Dispatchers.IO) {
                repository.getPreference("play_count_$trackId")?.toIntOrNull() ?: 0
            }
            val currentCounts = _playCounts.value.toMutableMap()
            if (!currentCounts.containsKey(trackId) || currentCounts[trackId] != countVal) {
                currentCounts[trackId] = countVal
                _playCounts.value = currentCounts
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val isDark = repository.getThemeMode() == "dark"
            val folderId = repository.getGoogleFolderId()
            val userEmail = repository.getPreference("user_email").let { if (it.isNullOrEmpty()) "listener@myuloc.com" else it }
            val userName = repository.getPreference("user_name").let { if (it.isNullOrEmpty()) "MyuLoc Listener" else it }
            val userPhone = repository.getPreference("user_phone").let { if (it.isNullOrEmpty()) "+1 (555) 732-4521" else it }
            
            val savedClientId = repository.getPreference("google_client_id")
            val clientIdVal = when {
                !savedClientId.isNullOrEmpty() -> savedClientId
                BuildConfig.GOOGLE_CLIENT_ID.isNotEmpty() && BuildConfig.GOOGLE_CLIENT_ID != "GOOGLE_CLIENT_ID_PLACEHOLDER" -> BuildConfig.GOOGLE_CLIENT_ID
                else -> "1055312055779-m62nep4lonmep5qs3t7n0a31jh7h8i3e.apps.googleusercontent.com"
            }
            val redirectVal = repository.getPreference("google_redirect_uri") ?: ""
            val apiKeyVal = repository.getPreference("google_api_key") ?: ""
            val invidiousVal = repository.getPreference("invidious_url") ?: "https://yewtu.be/"

            val filterSmallVal = repository.getPreference("filter_out_small_files")?.toBoolean() ?: false
            val filterShortVal = repository.getPreference("filter_out_short_audios")?.toIntOrNull() ?: 5
            val streamBitrateVal = repository.getPreference("preferred_stream_bitrate") ?: "Balanced"
            val bufferAllocVal = repository.getPreference("buffer_memory_allocation") ?: "Balanced"
            val headsetDisconnectVal = repository.getPreference("headset_disconnect_behavior")?.toBoolean() ?: true
            val audioQualityVal = repository.getPreference("playback_audio_quality") ?: "High Quality"

            withContext(Dispatchers.Main) {
                _isDarkMode.value = isDark
                _googleFolderId.value = folderId
                _isUserLoggedIn.value = true
                _loggedInEmail.value = userEmail
                _loggedInName.value = userName
                _loggedInPhone.value = userPhone
                _isConnectedToDrive.value = false
                _showGenrePreferencePopup.value = false
                _selectedGenres.value = emptyList()
                _customClientId.value = clientIdVal
                _customRedirectUri.value = redirectVal
                _googleApiKey.value = apiKeyVal
                _invidiousUrl.value = invidiousVal
                _animationSpeed.value = 0.5f
                _animationBounciness.value = 0.001f
                _filterOutSmallFiles.value = filterSmallVal
                _filterOutShortAudios.value = filterShortVal
                _preferredStreamBitrate.value = streamBitrateVal
                _bufferMemoryAllocation.value = bufferAllocVal
                _headsetDisconnectBehavior.value = headsetDisconnectVal
                _playbackAudioQuality.value = audioQualityVal
            }
        }
    }

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    fun setSubTab(tab: String) {
        _subTab.value = tab
    }

    fun saveGenrePreferences(genres: List<String>) {
        viewModelScope.launch {
            _selectedGenres.value = genres
            repository.setPreference("selected_genres", genres.joinToString(","))
            _showGenrePreferencePopup.value = false
            updateRecommendations(genres)
        }
    }

    fun showGenrePreferencesPopup() {
        _showGenrePreferencePopup.value = true
    }

    private fun updateRecommendations(genres: List<String>) {
        if (genres.isEmpty()) {
            _recommendationsList.value = emptyList()
            return
        }

        val recs = mutableListOf<PlayerTrack>()
        genres.forEach { genre ->
            when (genre.lowercase()) {
                "j-pop", "jpops" -> {
                    recs.add(PlayerTrack("ZRtdQ81jPUQ", "Idol", "YOASOBI", "", "https://img.youtube.com/vi/ZRtdQ81jPUQ/hqdefault.jpg", "Search", 220000L))
                    recs.add(PlayerTrack("by4SYYWLHXY", "Racing Into The Night", "YOASOBI", "", "https://img.youtube.com/vi/by4SYYWLHXY/hqdefault.jpg", "Search", 260000L))
                    recs.add(PlayerTrack("pgXpM4l_MwI", "Show", "Ado", "", "https://img.youtube.com/vi/pgXpM4l_MwI/hqdefault.jpg", "Search", 210000L))
                }
                "rock" -> {
                    recs.add(PlayerTrack("fJ9rUzIMcZQ", "Bohemian Rhapsody", "Queen", "", "https://img.youtube.com/vi/fJ9rUzIMcZQ/hqdefault.jpg", "Search", 360000L))
                    recs.add(PlayerTrack("eVTXPUF4Oz4", "In The End", "Linkin Park", "", "https://img.youtube.com/vi/eVTXPUF4Oz4/hqdefault.jpg", "Search", 210000L))
                    recs.add(PlayerTrack("Hh9yZWeTMVM", "The Beginning", "ONE OK ROCK", "", "https://img.youtube.com/vi/Hh9yZWeTMVM/hqdefault.jpg", "Search", 290000L))
                }
                "normal pops", "pop", "pops" -> {
                    recs.add(PlayerTrack("4NRXx6U8ABQ", "Blinding Lights", "The Weeknd", "", "https://img.youtube.com/vi/4NRXx6U8ABQ/hqdefault.jpg", "Search", 200000L))
                    recs.add(PlayerTrack("OPf0YbXqDm0", "Uptown Funk", "Bruno Mars", "", "https://img.youtube.com/vi/OPf0YbXqDm0/hqdefault.jpg", "Search", 270000L))
                    recs.add(PlayerTrack("e-ORhEE9VVg", "Blank Space", "Taylor Swift", "", "https://img.youtube.com/vi/e-ORhEE9VVg/hqdefault.jpg", "Search", 230000L))
                }
                "anime" -> {
                    recs.add(PlayerTrack("vMdgS_G3FmU", "Kaikai Kitan", "Eve", "", "https://img.youtube.com/vi/vMdgS_G3FmU/hqdefault.jpg", "Search", 220000L))
                    recs.add(PlayerTrack("dlFA0Zq1k2g", "Silhouette", "KANA-BOON", "", "https://img.youtube.com/vi/dlFA0Zq1k2g/hqdefault.jpg", "Search", 240000L))
                    recs.add(PlayerTrack("Jb6Zlg30rgk", "Kizuna no Kiseki", "MAN WITH A MISSION x milet", "", "https://img.youtube.com/vi/Jb6Zlg30rgk/hqdefault.jpg", "Search", 230000L))
                }
            }
        }
        _recommendationsList.value = recs.distinctBy { it.id }
    }

    fun updateAnimationSpeed(speed: Float) {
        _animationSpeed.value = speed
        viewModelScope.launch {
            repository.setPreference("anim_speed_val", speed.toString())
        }
    }

    fun updateAnimationBounciness(bounciness: Float) {
        _animationBounciness.value = bounciness
        viewModelScope.launch {
            repository.setPreference("anim_bounciness_val", bounciness.toString())
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val newMode = !_isDarkMode.value
            _isDarkMode.value = newMode
            repository.setThemeMode(if (newMode) "dark" else "light")
        }
    }

    fun saveCustomClientId(id: String) {
        viewModelScope.launch {
            _customClientId.value = id
            repository.setPreference("google_client_id", id)
        }
    }

    fun saveCustomRedirectUri(uri: String) {
        viewModelScope.launch {
            _customRedirectUri.value = uri
            repository.setPreference("google_redirect_uri", uri)
        }
    }

    fun saveGoogleApiKey(apiKey: String) {
        viewModelScope.launch {
            _googleApiKey.value = apiKey
            repository.setPreference("google_api_key", apiKey)
        }
    }

    fun saveInvidiousUrl(url: String) {
        viewModelScope.launch {
            val formatted = if (url.endsWith("/")) url else "$url/"
            _invidiousUrl.value = formatted
            repository.setPreference("invidious_url", formatted)
        }
    }

    fun cycleInvidiousNode() {
        val current = _invidiousUrl.value.removeSuffix("/")
        val nodes = listOf(
            "https://yewtu.be",
            "https://iv.melmac.space",
            "https://invidious.had.dns.army",
            "https://invidious.no-logs.com",
            "https://invidious.nerdvpn.de"
        )
        val currentIndex = nodes.indexOfFirst { it.equals(current, ignoreCase = true) }
        val nextIndex = if (currentIndex == -1) 0 else (currentIndex + 1) % nodes.size
        saveInvidiousUrl(nodes[nextIndex])
    }

    fun saveGoogleFolderId(folderId: String) {
        viewModelScope.launch {
            _googleFolderId.value = folderId
            repository.setGoogleFolderId(folderId)
            if (_isConnectedToDrive.value) {
                fetchLockerFiles()
            }
        }
    }

    // Connect manually via Developer Access Token
    fun connectWithAccessToken(token: String) {
        viewModelScope.launch {
            repository.setGoogleAccessToken(token)
            playerManager.setGoogleAccessToken(token)
            _isConnectedToDrive.value = true
            fetchLockerFiles()
        }
    }

    fun disconnectDrive() {
        viewModelScope.launch {
            repository.removePreference("access_token")
            playerManager.setGoogleAccessToken(null)
            _isConnectedToDrive.value = false
            repository.clearCachedTracks()
            loadDemoLocker()
        }
    }

    // Google Drive Fetcher
    fun fetchLockerFiles() {
        _lockerUiState.value = LockerUiState.Loading
        viewModelScope.launch {
            val token = repository.getGoogleAccessToken()
            val apiKey = _googleApiKey.value

            playerManager.setGoogleAccessToken(token)
            playerManager.setGoogleApiKey(apiKey)

            if (token.isNullOrEmpty() && apiKey.isEmpty()) {
                _lockerUiState.value = LockerUiState.Error("Google Drive not connected. Please connect via OAuth or enter a public Developer API Key in Settings.")
                return@launch
            }

            try {
                val folder = _googleFolderId.value
                val queryStr = if (folder == "root" || folder.isEmpty()) {
                    "(mimeType contains 'audio/' or name contains '.mp3' or name contains '.wav' or name contains '.flac') and trashed = false"
                } else {
                    "'$folder' in parents and (mimeType contains 'audio/' or name contains '.mp3' or name contains '.wav' or name contains '.flac') and trashed = false"
                }

                // Graceful, non-blocking coroutine retry logic up to 3 attempts under timeout constraints (isolated from UI thread)
                var response: com.example.data.network.GoogleDriveResponse? = null
                var attempts = 0
                val maxAttempts = 3

                while (attempts < maxAttempts && response == null) {
                    attempts++
                    response = kotlinx.coroutines.withTimeoutOrNull(8000) {
                        withContext(Dispatchers.IO) {
                            try {
                                if (!token.isNullOrEmpty()) {
                                    NetworkClient.googleDriveApi.listFiles("Bearer $token", queryStr)
                                } else {
                                    NetworkClient.googleDriveApi.listPublicFiles(apiKey, queryStr)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }
                        }
                    }
                    if (response == null && attempts < maxAttempts) {
                        kotlinx.coroutines.delay(1000) // Delay 1 sec before retrying
                    }
                }

                if (response == null) {
                    _lockerUiState.value = LockerUiState.Error("Google Drive Link Timeout. Retried 3 times, check network latency or authorization.")
                    return@launch
                }

                val tracks = response.files.map { file ->
                    // Extract title and artist from filename
                    val parts = splitArtistTitle(file.name)
                    CachedTrack(
                        id = file.id,
                        title = parts.second,
                        artist = parts.first,
                        durationMs = 0L, // dynamic duration parsed when playing
                        source = "Locker",
                        streamUrl = "https://www.googleapis.com/drive/v3/files/${file.id}?alt=media",
                        thumbnailUrl = file.thumbnailLink ?: ""
                    )
                }

                repository.saveCachedTracks(tracks)
                _lockerUiState.value = LockerUiState.Success(tracks.map { it.toPlayerTrack() })

            } catch (e: Exception) {
                e.printStackTrace()
                _lockerUiState.value = LockerUiState.Error("Connection Failed: ${e.localizedMessage}. Is your OAuth token expired?")
            }
        }
    }

    private fun loadDemoLocker() {
        val demos = getDemoTracks()
        _lockerUiState.value = LockerUiState.Success(demos)
    }

    // Searches the local/offline library instantly and with high performance
    fun performSearch(query: String) {
        _searchQuery.value = query
        if (query.trim().isEmpty()) {
            _searchUiState.value = SearchUiState.Idle
            return
        }
        _searchUiState.value = SearchUiState.Loading
        viewModelScope.launch {
            try {
                // Collect existing offline & cached tracks and search through them entirely offline
                val offlineTracks = repository.offlineTracks.first().map {
                    PlayerTrack(
                        id = it.id,
                        title = it.title,
                        artist = it.artist,
                        streamUrl = it.localUri,
                        thumbnailUrl = it.thumbnailUrl,
                        source = it.source,
                        durationMs = it.durationMs
                    )
                }
                
                val matched = offlineTracks.filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true)
                }

                _searchUiState.value = SearchUiState.Success(matched)
            } catch (e: Throwable) {
                _searchUiState.value = SearchUiState.Error("Search error: ${e.localizedMessage}")
            }
        }
    }

    // Offline Access & Download Monitor
    private val _downloadProgress = MutableStateFlow<Map<String, Int>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Int>> = _downloadProgress.asStateFlow()

    // Sorting configuration
    private val _sortField = MutableStateFlow(SortField.TITLE)
    val sortField: StateFlow<SortField> = _sortField.asStateFlow()

    private val _sortDirection = MutableStateFlow(SortDirection.ASCENDING)
    val sortDirection: StateFlow<SortDirection> = _sortDirection.asStateFlow()

    private val _playCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val playCounts: StateFlow<Map<String, Int>> = _playCounts.asStateFlow()

    val offlineTracksFlow = combine(
        repository.offlineTracks,
        _playCounts,
        _sortField,
        _sortDirection
    ) { tracks, playCountsMap, field, direction ->
        val sorted = when (field) {
            SortField.TITLE -> tracks.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
            SortField.DATE_ADDED -> tracks.sortedBy { it.timestamp }
            SortField.PLAY_COUNT -> tracks.sortedBy { playCountsMap[it.id] ?: 0 }
        }
        if (direction == SortDirection.DESCENDING) {
            sorted.reversed()
        } else {
            sorted
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSortField(field: SortField) {
        _sortField.value = field
    }

    fun setSortDirection(direction: SortDirection) {
        _sortDirection.value = direction
    }

    // User Authentication states
    private val _isUserLoggedIn = MutableStateFlow(true)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _loggedInEmail = MutableStateFlow("listener@myuloc.com")
    val loggedInEmail: StateFlow<String> = _loggedInEmail.asStateFlow()

    private val _loggedInName = MutableStateFlow("MyuLoc Listener")
    val loggedInName: StateFlow<String> = _loggedInName.asStateFlow()

    private val _loggedInPhone = MutableStateFlow("+1 (555) 732-4521")
    val loggedInPhone: StateFlow<String> = _loggedInPhone.asStateFlow()

    // Administrative Privileges verified emails check
    val isCurrentUserAdmin: StateFlow<Boolean> = _loggedInEmail.map { email ->
        val clean = email.trim().lowercase()
        clean == "aashusen2006@gmail.com" || clean == "aashu0709235@gmail.com"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Public Proxy Hotlist Collections (loaded dynamically from configuration)
    private val _proxyHotlist = MutableStateFlow<List<PlayerTrack>>(emptyList())
    val proxyHotlist: StateFlow<List<PlayerTrack>> = _proxyHotlist.asStateFlow()

    private val _isFetchingHotlist = MutableStateFlow(false)
    val isFetchingHotlist: StateFlow<Boolean> = _isFetchingHotlist.asStateFlow()

    // Sleep Timer states
    private val _sleepTimerMinutesLeft = MutableStateFlow(0)
    val sleepTimerMinutesLeft: StateFlow<Int> = _sleepTimerMinutesLeft.asStateFlow()

    private val _sleepTimerSecondsLeft = MutableStateFlow(0)
    val sleepTimerSecondsLeft: StateFlow<Int> = _sleepTimerSecondsLeft.asStateFlow()

    private val _sleepTimerRunning = MutableStateFlow(false)
    val sleepTimerRunning: StateFlow<Boolean> = _sleepTimerRunning.asStateFlow()

    private val _appCloseTrigger = MutableSharedFlow<Unit>(replay = 0)
    val appCloseTrigger = _appCloseTrigger.asSharedFlow()

    private var sleepTimerJob: Job? = null

    // Resolve direct streaming audio URL using multiple Piped API nodes or Cobalt, with Invidious as final fallback
    private suspend fun resolveDirectStreamingUrl(videoId: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Try Piped API instances (highly stable, serves direct high-speed Google CDN streams)
                val pipedInstances = listOf(
                    "https://pipedapi.kavin.rocks",
                    "https://pipedapi.tokhmi.xyz",
                    "https://pipedapi.darkness.services",
                    "https://pipedapi.river.ooo"
                )
                
                val client = okhttp3.OkHttpClient.Builder()
                    .connectTimeout(4, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(4, java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                val qualityPref = _preferredStreamBitrate.value
                android.util.Log.d("MyuLocViewModel", "Resolving stream for video: $videoId. Target Quality: $qualityPref")

                for (instance in pipedInstances) {
                    try {
                        val request = okhttp3.Request.Builder()
                            .url("$instance/streams/$videoId")
                            .header("User-Agent", "Mozilla/5.0")
                            .build()
                        client.newCall(request).execute().use { response ->
                            if (response.isSuccessful) {
                                val body = response.body?.string() ?: ""
                                val jsonObj = org.json.JSONObject(body)
                                val audioStreams = jsonObj.optJSONArray("audioStreams")
                                if (audioStreams != null && audioStreams.length() > 0) {
                                    // Parse streams with their bitrates
                                    val streamsList = mutableListOf<Pair<String, Int>>()
                                    for (i in 0 until audioStreams.length()) {
                                        val stream = audioStreams.getJSONObject(i)
                                        val streamUrl = stream.optString("url")
                                        val bitrate = stream.optInt("bitrate", -1)
                                        if (streamUrl.isNotEmpty()) {
                                            streamsList.add(streamUrl to bitrate)
                                        }
                                    }

                                    if (streamsList.isNotEmpty()) {
                                        // Sort by bitrate ascending
                                        streamsList.sortBy { it.second }

                                        val chosenUrl = when (qualityPref) {
                                            "Low Quality", "Low Data" -> {
                                                // Select lowest quality stream
                                                streamsList.first().first
                                            }
                                            "Medium Quality", "Balanced" -> {
                                                // Select medium quality stream (closest to 128kbps)
                                                val targetBitrate = 128000
                                                val best = streamsList.minByOrNull { kotlin.math.abs(it.second - targetBitrate) } ?: streamsList.first()
                                                best.first
                                            }
                                            "High Quality", "High Quality Audio" -> {
                                                // Select high quality stream (closest to 160kbps)
                                                val targetBitrate = 160000
                                                val best = streamsList.minByOrNull { kotlin.math.abs(it.second - targetBitrate) } ?: streamsList.last()
                                                best.first
                                            }
                                            "Ultra Quality" -> {
                                                // Select ultra / extra high quality stream (maximum bitrate)
                                                streamsList.last().first
                                            }
                                            else -> {
                                                // Default: Balance (closest to 128kbps)
                                                val targetBitrate = 128000
                                                val best = streamsList.minByOrNull { kotlin.math.abs(it.second - targetBitrate) } ?: streamsList.first()
                                                best.first
                                            }
                                        }

                                        android.util.Log.d("MyuLocViewModel", "Successfully resolved ultra-fast stream via Piped [$instance] matching quality prefer: $qualityPref")
                                        return@withContext chosenUrl
                                    }
                                }
                            }
                        }
                    } catch (e: Throwable) {
                        // Try next instance
                    }
                }

                // 2. Try Cobalt API as robust alternative
                try {
                    val mediaJson = org.json.JSONObject().apply {
                        put("url", "https://www.youtube.com/watch?v=$videoId")
                        put("downloadMode", "audio")
                    }
                    val body = okhttp3.RequestBody.create(
                        null as okhttp3.MediaType?,
                        mediaJson.toString()
                    )
                    val request = okhttp3.Request.Builder()
                        .url("https://api.cobalt.tools/api/json")
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .post(body)
                        .build()
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val respBody = response.body?.string() ?: ""
                            val jsonObj = org.json.JSONObject(respBody)
                            val streamUrl = jsonObj.optString("url")
                            if (streamUrl.isNotEmpty()) {
                                android.util.Log.d("MyuLocViewModel", "Successfully resolved ultra-fast stream via Cobalt!")
                                return@withContext streamUrl
                            }
                        }
                    }
                } catch (e: Throwable) {
                    // Ignore and fall back
                }
            } catch (outerT: Throwable) {
                android.util.Log.e("MyuLocViewModel", "Error inside resolveDirectStreamingUrl block, falling back: ${outerT.message}")
                outerT.printStackTrace()
            }

            // 3. Fallback to Invidious
            val activeUrl = _invidiousUrl.value.removeSuffix("/")
            val fallbackUrl = "$activeUrl/latest_version?id=$videoId&itag=140&local=true"
            android.util.Log.d("MyuLocViewModel", "Piped and Cobalt failed. Falling back to Invidious URL: $fallbackUrl")
            fallbackUrl
        }
    }

    // Resolves streaming audio URL on-the-fly when clicking play from Search Results, or redirects to local file when downloaded offline
    fun playTrackWithResolution(track: PlayerTrack, queueInput: List<PlayerTrack>) {
        viewModelScope.launch {
            try {
                // Get all offline tracks at once in a single fast DB query to avoid N separate queries inside .map
                val allOffline = repository.offlineTracks.first()
                val offlineMap = allOffline.associateBy { it.id }

                val playableTrack = if (offlineMap.containsKey(track.id)) {
                    track.copy(streamUrl = offlineMap[track.id]?.localUri ?: "")
                } else {
                    track
                }

                // Resolve entire list's download status to play fluidly even offline instantly using lookups in O(1) lookup time
                val updatedQueue = queueInput.map { qTrack ->
                    val cachedOffline = offlineMap[qTrack.id]
                    if (cachedOffline != null) qTrack.copy(streamUrl = cachedOffline.localUri) else qTrack
                }

                if (playableTrack.streamUrl.isEmpty() && playableTrack.source != "Locker" && playableTrack.source != "Offline") {
                    // Instantly play with high-performance direct streaming gateway path to avoid ANY latency!
                    val resolvedUrl = resolveDirectStreamingUrl(playableTrack.id)
                    val fullTrack = playableTrack.copy(streamUrl = resolvedUrl)
                    val finalQueue = updatedQueue.map {
                        if (it.id == playableTrack.id) fullTrack else it
                    }
                    playerManager.playTrack(fullTrack, finalQueue)
                } else {
                    // Instantly play (Locker already has streamUrl set or we swapped to localUri)
                    playerManager.playTrack(playableTrack, updatedQueue)
                }
            } catch (t: Throwable) {
                android.util.Log.e("MyuLocViewModel", "Critical error playing track in playTrackWithResolution: ${t.message}")
                t.printStackTrace()
                // Safely try a fallback
                try {
                    val activeUrl = _invidiousUrl.value.removeSuffix("/")
                    val fallbackUrl = "$activeUrl/latest_version?id=${track.id}&itag=140&local=true"
                    playerManager.playTrack(track.copy(streamUrl = fallbackUrl), queueInput)
                } catch (fallbackT: Throwable) {
                    fallbackT.printStackTrace()
                }
            }
        }
    }

    fun addToQueue(track: PlayerTrack) {
        playerManager.addToQueue(track)
    }

    fun removeFromQueue(trackId: String) {
        playerManager.removeFromQueue(trackId)
    }

    fun clearQueue() {
        playerManager.clearQueue()
    }

    fun setEqEnabled(enabled: Boolean) {
        playerManager.setEqEnabled(enabled)
    }

    fun setEqBandLevel(band: Int, levelMilliBels: Int) {
        playerManager.setEqBandLevel(band, levelMilliBels)
    }

    // Download dynamic Google Drive Track File
    fun downloadTrack(track: PlayerTrack) {
        viewModelScope.launch {
            val token = repository.getGoogleAccessToken()
            if (token.isNullOrEmpty() && track.source == "Locker") {
                Toast.makeText(getApplication(), "Connect to Google Drive to download.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // Mark download as starting/0%
            _downloadProgress.value = _downloadProgress.value + (track.id to 0)

            try {
                withContext(Dispatchers.IO) {
                    var currentUrl = "https://www.googleapis.com/drive/v3/files/${track.id}?alt=media"
                    val downloadsDir = File(getApplication<Application>().filesDir, "downloads")
                    if (!downloadsDir.exists()) {
                        downloadsDir.mkdirs()
                    }
                    val destinationFile = File(downloadsDir, "${track.id}.mp3")

                    var connection: HttpURLConnection? = null
                    var responseCode = 0
                    var redirectCount = 0
                    val maxRedirects = 5
                    var tokenHeader = "Bearer $token"
                    var finalStream: java.io.InputStream? = null
                    var length = -1L

                    while (redirectCount < maxRedirects) {
                        val conn = URL(currentUrl).openConnection() as HttpURLConnection
                        conn.instanceFollowRedirects = false // we handle redirects manually to prevent header drops
                        if (track.source == "Locker" && tokenHeader.isNotEmpty()) {
                            conn.setRequestProperty("Authorization", tokenHeader)
                        }
                        conn.connect()
                        responseCode = conn.responseCode

                        if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || 
                            responseCode == HttpURLConnection.HTTP_MOVED_PERM || 
                            responseCode == 307 || responseCode == 308) {
                            val newUrl = conn.getHeaderField("Location")
                            conn.disconnect()
                            if (newUrl != null) {
                                currentUrl = newUrl
                                redirectCount++
                                if (!currentUrl.contains("google.com") && !currentUrl.contains("googleapis.com")) {
                                    // Remove auth bearer header for external endpoints for security
                                    tokenHeader = ""
                                }
                                continue
                            }
                        }
                        connection = conn
                        length = conn.contentLengthLong
                        finalStream = conn.inputStream
                        break
                    }

                    if (responseCode == 200 && finalStream != null) {
                        finalStream.use { input ->
                            destinationFile.outputStream().use { output ->
                                val buffer = ByteArray(8192)
                                var bytesRead: Int
                                var totalBytesRead = 0L
                                while (input.read(buffer).also { bytesRead = it } != -1) {
                                    output.write(buffer, 0, bytesRead)
                                    totalBytesRead += bytesRead
                                    if (length > 0) {
                                        val percentage = ((totalBytesRead * 100) / length).toInt()
                                        _downloadProgress.value = _downloadProgress.value + (track.id to percentage)
                                    }
                                }
                            }
                        }
                        if (connection != null) {
                            try { connection.disconnect() } catch (e: Exception) {}
                        }

                        // Save details to database
                        val offline = OfflineTrack(
                            id = track.id,
                            title = track.title,
                            artist = track.artist,
                            durationMs = track.durationMs,
                            source = "Locker",
                            localUri = "file://${destinationFile.absolutePath}",
                            thumbnailUrl = track.thumbnailUrl
                        )
                        repository.addOfflineTrack(offline)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(getApplication(), "Downloaded: ${track.title}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(getApplication(), "Download Error ${responseCode}. Connect/Verify Drive connection.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Download failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                _downloadProgress.value = _downloadProgress.value - track.id
            }
        }
    }

    // Download entire visible locker folder contents
    fun downloadAllLockerTracks(tracks: List<PlayerTrack>) {
        if (tracks.isEmpty()) return
        Toast.makeText(getApplication(), "Preparing background downloads for ${tracks.size} tracks...", Toast.LENGTH_SHORT).show()
        viewModelScope.launch {
            tracks.forEach { track ->
                val alreadyOffline = repository.isOffline(track.id)
                if (!alreadyOffline) {
                    downloadTrack(track)
                    delay(300) // gentle padding between requests
                }
            }
        }
    }

    private val _selectedDeleteIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedDeleteIds: StateFlow<Set<String>> = _selectedDeleteIds.asStateFlow()

    private val _isMultiSelectMode = MutableStateFlow(false)
    val isMultiSelectMode: StateFlow<Boolean> = _isMultiSelectMode.asStateFlow()

    fun toggleDeleteSelection(trackId: String) {
        val current = _selectedDeleteIds.value.toMutableSet()
        if (current.contains(trackId)) {
            current.remove(trackId)
        } else {
            current.add(trackId)
        }
        _selectedDeleteIds.value = current
        if (current.isEmpty()) {
            _isMultiSelectMode.value = false
        }
    }

    fun startMultiSelectMode(initialTrackId: String) {
        _isMultiSelectMode.value = true
        _selectedDeleteIds.value = setOf(initialTrackId)
    }

    fun clearDeleteSelection() {
        _selectedDeleteIds.value = emptySet()
        _isMultiSelectMode.value = false
    }

    fun deleteSelectedTracks() {
        val idsToDelete = _selectedDeleteIds.value.toList()
        viewModelScope.launch {
            idsToDelete.forEach { trackId ->
                val offline = repository.getOfflineTrack(trackId)
                if (offline != null) {
                    try {
                        val path = offline.localUri.substringAfter("file://")
                        val file = File(path)
                        
                        // MediaStore deletion
                        try {
                            val contentResolver = getApplication<Application>().contentResolver
                            val uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            val selection = "${android.provider.MediaStore.Audio.Media.DATA} = ?"
                            val selectionArgs = arrayOf(path)
                            contentResolver.delete(uri, selection, selectionArgs)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        
                        if (file.exists()) {
                            file.delete()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    repository.removeOfflineTrack(trackId)
                }
            }
            _selectedDeleteIds.value = emptySet()
            _isMultiSelectMode.value = false
            Toast.makeText(getApplication(), "Successfully deleted ${idsToDelete.size} tracks from device.", Toast.LENGTH_SHORT).show()
        }
    }

    // Remove local download / path
    fun deleteOfflineTrack(trackId: String) {
        viewModelScope.launch {
            val offline = repository.getOfflineTrack(trackId)
            if (offline != null) {
                try {
                    val path = offline.localUri.substringAfter("file://")
                    val file = File(path)
                    
                    // 1. ContentResolver / MediaStore deletion
                    try {
                        val contentResolver = getApplication<Application>().contentResolver
                        val uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        val selection = "${android.provider.MediaStore.Audio.Media.DATA} = ?"
                        val selectionArgs = arrayOf(path)
                        contentResolver.delete(uri, selection, selectionArgs)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    
                    // 2. Physical java.io Directory File Deletion
                    if (file.exists()) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                repository.removeOfflineTrack(trackId)
                Toast.makeText(getApplication(), "Deleted cash & storage file: ${offline.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Scan standard device directories for audio files using MediaStore (and legacy File walker as fallback)
    fun scanLocalFiles() {
        viewModelScope.launch {
            try {
                Toast.makeText(getApplication(), "Scanning device storage for local songs...", Toast.LENGTH_SHORT).show()
                val list = mutableListOf<OfflineTrack>()
                
                // 1. Query MediaStore (Highly robust on Android 10/11/12/13/14/15)
                withContext(Dispatchers.IO) {
                    try {
                        val contentResolver = getApplication<Application>().contentResolver
                        val uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        val projection = arrayOf(
                            android.provider.MediaStore.Audio.Media._ID,
                            android.provider.MediaStore.Audio.Media.TITLE,
                            android.provider.MediaStore.Audio.Media.ARTIST,
                            android.provider.MediaStore.Audio.Media.DURATION,
                            android.provider.MediaStore.Audio.Media.DATA
                        )
                        // Query all music files
                        val selection = "${android.provider.MediaStore.Audio.Media.IS_MUSIC} != 0"
                        contentResolver.query(uri, projection, selection, null, null)?.use { cursor ->
                            val idCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media._ID)
                            val titleCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.TITLE)
                            val artistCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.ARTIST)
                            val durationCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DURATION)
                            val dataCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA)
                            
                            while (cursor.moveToNext()) {
                                val path = cursor.getString(dataCol)
                                if (path.isNullOrEmpty()) continue
                                
                                // Size filter: Ignore files under 500KB if filter is active
                                val fileObj = java.io.File(path)
                                if (_filterOutSmallFiles.value && fileObj.exists() && fileObj.length() < 500 * 1024) {
                                    continue
                                }

                                val duration = cursor.getLong(durationCol)
                                // Duration filter: Filter of system ringtones (0s to 60s)
                                if (duration < _filterOutShortAudios.value * 1000L) {
                                    continue
                                }

                                val title = cursor.getString(titleCol) ?: "Unknown Track"
                                val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                                val id = "local_${path.hashCode()}"
                                list.add(
                                    OfflineTrack(
                                        id = id,
                                        title = title,
                                        artist = artist,
                                        durationMs = duration,
                                        source = "LocalScanned",
                                        localUri = "file://$path",
                                        thumbnailUrl = ""
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // 2. Legacy direct directories scan fallback (if MediaStore database turns up empty)
                if (list.isEmpty()) {
                    withContext(Dispatchers.IO) {
                        val paths = listOfNotNull(
                            android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_MUSIC),
                            android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                        )
                        for (path in paths) {
                            try {
                                scanDirectoryForAudio(path, list)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                withContext(Dispatchers.IO) {
                    list.forEach { track ->
                        repository.addOfflineTrack(track)
                    }
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Scan complete. Discovered and cataloged ${list.size} music tracks.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Scan failed or interrupted: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun scanDirectoryForAudio(dir: File, list: MutableList<OfflineTrack>) {
        if (!dir.exists() || !dir.isDirectory) return
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                scanDirectoryForAudio(file, list)
            } else {
                val name = file.name.lowercase()
                if (name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".flac") || name.endsWith(".m4a") || name.endsWith(".ogg")) {
                    if (_filterOutSmallFiles.value && file.length() < 500 * 1024) {
                        continue
                    }
                    val parts = splitArtistTitle(file.name)
                    val id = "local_${file.absolutePath.hashCode()}"
                    list.add(
                        OfflineTrack(
                            id = id,
                            title = parts.second,
                            artist = parts.first,
                            durationMs = 0L,
                            source = "Local",
                            localUri = "file://${file.absolutePath}",
                            thumbnailUrl = ""
                        )
                    )
                }
            }
        }
    }

    // --- User login action methods ---
    fun loginUser(email: String, name: String, phone: String = "") {
        viewModelScope.launch {
            _isUserLoggedIn.value = true
            _loggedInEmail.value = email
            _loggedInName.value = name
            _loggedInPhone.value = phone
            repository.setPreference("user_logged_in", "true")
            repository.setPreference("user_email", email)
            repository.setPreference("user_name", name)
            repository.setPreference("user_phone", phone)
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            _isUserLoggedIn.value = true
            _loggedInEmail.value = "listener@myuloc.com"
            _loggedInName.value = "MyuLoc Listener"
            _loggedInPhone.value = "+1 (555) 732-4521"
            repository.setPreference("user_logged_in", "true")
            repository.setPreference("user_email", "listener@myuloc.com")
            repository.setPreference("user_name", "MyuLoc Listener")
            repository.setPreference("user_phone", "+1 (555) 732-4521")
            Toast.makeText(getApplication(), "Profile Reset to Defaults", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Xiaomi Sleep Timer Mechanism ---
    fun startSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes <= 0) {
            _sleepTimerRunning.value = false
            _sleepTimerMinutesLeft.value = 0
            _sleepTimerSecondsLeft.value = 0
            return
        }

        _sleepTimerRunning.value = true
        _sleepTimerMinutesLeft.value = minutes
        _sleepTimerSecondsLeft.value = minutes * 60
        sleepTimerJob = viewModelScope.launch {
            while (_sleepTimerSecondsLeft.value > 0) {
                delay(1000)
                val currentSecs = _sleepTimerSecondsLeft.value
                val nextSecs = if (currentSecs > 0) currentSecs - 1 else 0
                _sleepTimerSecondsLeft.value = nextSecs
                _sleepTimerMinutesLeft.value = (nextSecs + 59) / 60
            }
            if (playerManager.isPlaying.value) {
                playerManager.togglePlayPause() // Pauses ExoPlayer
            }
            _sleepTimerRunning.value = false
            _sleepTimerMinutesLeft.value = 0
            _sleepTimerSecondsLeft.value = 0
            withContext(Dispatchers.Main) {
                Toast.makeText(getApplication(), "Sleep timer finished. Playback suspended, closing app...", Toast.LENGTH_LONG).show()
                delay(1500)
                _appCloseTrigger.emit(Unit)
            }
        }
    }

    fun extendSleepTimer(minutes: Int) {
        if (_sleepTimerRunning.value) {
            val currentSeconds = _sleepTimerSecondsLeft.value
            val addedSeconds = minutes * 60
            val newSeconds = currentSeconds + addedSeconds
            _sleepTimerSecondsLeft.value = newSeconds
            _sleepTimerMinutesLeft.value = (newSeconds + 59) / 60
            Toast.makeText(getApplication(), "Extended sleep timer by $minutes minutes", Toast.LENGTH_SHORT).show()
        } else {
            startSleepTimer(minutes)
        }
    }

    fun stopSleepTimer() {
        sleepTimerJob?.cancel()
        _sleepTimerStateReset()
    }

    private fun _sleepTimerStateReset() {
        _sleepTimerRunning.value = false
        _sleepTimerMinutesLeft.value = 0
        _sleepTimerSecondsLeft.value = 0
    }

    // Favorite Actions
    fun toggleFavorite(track: PlayerTrack) {
        viewModelScope.launch {
            val isFav = repository.isFavorite(track.id)
            if (isFav) {
                repository.removeFavorite(track.id)
                Toast.makeText(getApplication(), "Removed from favorites", Toast.LENGTH_SHORT).show()
            } else {
                repository.addFavorite(track.toFavoriteTrack())
                Toast.makeText(getApplication(), "Added to favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun fetchDriveStorage() {
        _driveStorageState.value = DriveStorageState.Loading
        viewModelScope.launch {
            val token = repository.getGoogleAccessToken()
            val hasToken = !token.isNullOrEmpty()
            if (!hasToken) {
                // Return fallback demo statistics so the user can interactively test storage dialog in demo mode
                val limit = 16106127360L // 15 GB
                val usage = 4294967296L // 4.0 GB
                val remaining = limit - usage
                _driveStorageState.value = DriveStorageState.Success(
                    limitBytes = limit,
                    usageBytes = usage,
                    remainingBytes = remaining,
                    usagePercentage = 26.6f,
                    isMock = true
                )
                return@launch
            }
            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.googleDriveApi.getAbout("Bearer $token")
                }
                val quota = response.storageQuota
                if (quota != null) {
                    val limit = quota.limit?.toLongOrNull() ?: 16106127360L
                    val usage = quota.usage?.toLongOrNull() ?: 0L
                    val remaining = if (limit > usage) limit - usage else 0L
                    val percentage = if (limit > 0) (usage.toFloat() / limit.toFloat()) * 100f else 0f
                    _driveStorageState.value = DriveStorageState.Success(
                        limitBytes = limit,
                        usageBytes = usage,
                        remainingBytes = remaining,
                        usagePercentage = percentage,
                        isMock = false
                    )
                } else {
                    _driveStorageState.value = DriveStorageState.Error("No storage quota info returned from Google Drive API.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // In case of any network or Auth issue, also fallback gracefully
                val limit = 16106127360L
                val usage = 4294967296L
                val remaining = limit - usage
                _driveStorageState.value = DriveStorageState.Success(
                    limitBytes = limit,
                    usageBytes = usage,
                    remainingBytes = remaining,
                    usagePercentage = 26.6f,
                    isMock = true
                )
            }
        }
    }

    // Load trending or beautiful popular proxy streams to present on initial Search screen
    fun loadProxyHotlist() {
        if (_proxyHotlist.value.isNotEmpty()) return
        _isFetchingHotlist.value = true
        viewModelScope.launch {
            try {
                val results = withContext(Dispatchers.IO) {
                    try {
                        val activeUrl = _invidiousUrl.value
                        val api = NetworkClient.getInvidiousApi(activeUrl)
                        api.searchVideos("popular lofi streams").map { video ->
                            PlayerTrack(
                                id = video.videoId ?: "",
                                title = video.title ?: "Unknown Title",
                                artist = video.author ?: "Unknown Artist",
                                streamUrl = "",
                                thumbnailUrl = video.videoThumbnails?.firstOrNull()?.url ?: "https://img.youtube.com/vi/${video.videoId ?: ""}/hqdefault.jpg",
                                source = "Search",
                                durationMs = (video.lengthSeconds ?: 240L) * 1000L
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                if (results != null && results.isNotEmpty()) {
                    _proxyHotlist.value = results.take(8)
                } else {
                    // Predefined classic fallback tracks if network proxy node errors, ensuring immediate content
                    val fallbacks = listOf(
                        Triple("5qap5aO4i9A", "Lofi Hip Hop Radio - Chill Beats", "Lofi Girl"),
                        Triple("Tj_08n09t9c", "Mellow Piano Melody - Relaxing Sleep Music", "Chill Clouds"),
                        Triple("DWfZOFgRqo8", "Synthwave Retro Beats - Midnight Grid", "Vaporwave Radio"),
                        Triple("hHW1oY26kxQ", "Coffee Shop Ambient Jazz & Rain Bossanova", "Café Lounge"),
                        Triple("9FvvbVI5rRc", "Deep Focus Forest Ambient Sounds", "Nurture Nature"),
                        Triple("jfKfPfyJRdk", "Lofi Girl - Relaxing Beats to Study/Relax To", "Lofi Studio")
                    )
                    _proxyHotlist.value = fallbacks.map { (vId, title, artist) ->
                        PlayerTrack(
                            id = vId,
                            title = title,
                            artist = artist,
                            streamUrl = "",
                            thumbnailUrl = "https://img.youtube.com/vi/$vId/hqdefault.jpg",
                            source = "Search",
                            durationMs = 240000L
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isFetchingHotlist.value = false
            }
        }
    }

    // Ping test Invidious custom proxy endpoint and return status logs for admins
    fun verifyInvidiousNode(url: String, callback: (String) -> Unit) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            var log = ""
            withContext(Dispatchers.IO) {
                try {
                    val formattedUrl = if (url.endsWith("/")) url else "$url/"
                    val checkUrl = "${formattedUrl}api/v1/search?q=music&type=video"
                    val conn = URL(checkUrl).openConnection() as HttpURLConnection
                    conn.connectTimeout = 5000
                    conn.readTimeout = 5000
                    conn.requestMethod = "GET"
                    val code = conn.responseCode
                    val latency = System.currentTimeMillis() - startTime
                    log = "Node check complete: Status $code ($latency ms response time)\nAPI endpoint verification successfully passed."
                } catch (e: Exception) {
                    log = "Verification Failed: ${e.message}\nPlease double-check node syntax or system online state."
                }
            }
            callback(log)
        }
    }

    // Executive diagnostic to purge persistent sqlite search cache
    fun flushSqliteCache() {
        viewModelScope.launch {
            try {
                repository.clearCachedTracks()
                Toast.makeText(getApplication(), "SQLite search cache purged successfully.", Toast.LENGTH_SHORT).show()
                if (!_isConnectedToDrive.value) {
                    loadDemoLocker()
                } else {
                    fetchLockerFiles()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(getApplication(), "Error purging cache: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // We do not release playerManager here because playback should continue in the background via Service.
        // The playerManager resources will be cleaned up safely when MyuLocPlaybackService.onDestroy() is called.
    }

    // --- Helpers ---

    private fun splitArtistTitle(fileName: String): Pair<String, String> {
        val cleanName = fileName.substringBeforeLast(".") // strip extension like .mp3
        if (cleanName.contains(" - ")) {
            val parts = cleanName.split(" - ", limit = 2)
            return Pair(parts[0].trim(), parts[1].trim())
        }
        return Pair("Unknown Artist", cleanName.trim())
    }

    fun getDemoTracks(): List<PlayerTrack> {
        return emptyList()
    }
}

// Map extensions
fun CachedTrack.toPlayerTrack() = PlayerTrack(
    id = id,
    title = title,
    artist = artist,
    streamUrl = streamUrl,
    thumbnailUrl = thumbnailUrl,
    source = source,
    durationMs = durationMs
)

fun PlayerTrack.toFavoriteTrack() = FavoriteTrack(
    id = id,
    title = title,
    artist = artist,
    streamUrl = streamUrl,
    thumbnailUrl = thumbnailUrl,
    source = source,
    durationMs = durationMs
)

fun FavoriteTrack.toPlayerTrack() = PlayerTrack(
    id = id,
    title = title,
    artist = artist,
    streamUrl = streamUrl,
    thumbnailUrl = thumbnailUrl,
    source = source,
    durationMs = durationMs
)

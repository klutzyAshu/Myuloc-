package com.example.viewmodel

import com.example.BuildConfig
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.CachedTrack
import com.example.data.database.FavoriteTrack
import com.example.data.database.MyuLocDatabase
import com.example.data.database.OfflineTrack
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import com.example.ui.controller.ToastNotificationManager
import com.example.ui.controller.ToastType
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

    // Light/Dark Theme Preference & Custom Themes Persistent Registry
    private val themePrefs = application.getSharedPreferences("myuloc_theme_settings", Context.MODE_PRIVATE)

    private val _isDarkMode = MutableStateFlow(themePrefs.getBoolean("is_dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Custom theme state
    private val _customThemeEnabled = MutableStateFlow(themePrefs.getBoolean("custom_theme_enabled", false))
    val customThemeEnabled: StateFlow<Boolean> = _customThemeEnabled.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(themePrefs.getBoolean("dynamic_color_enabled", false))
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    private val _artworkThemeEnabled = MutableStateFlow(themePrefs.getBoolean("artwork_theme_enabled", false))
    val artworkThemeEnabled: StateFlow<Boolean> = _artworkThemeEnabled.asStateFlow()

    private val _customHue = MutableStateFlow(themePrefs.getFloat("custom_hue", 200f))
    val customHue: StateFlow<Float> = _customHue.asStateFlow()

    private val _customSaturation = MutableStateFlow(themePrefs.getFloat("custom_saturation", 0.80f))
    val customSaturation: StateFlow<Float> = _customSaturation.asStateFlow()

    private val _customLightness = MutableStateFlow(themePrefs.getFloat("custom_lightness", 0.15f))
    val customLightness: StateFlow<Float> = _customLightness.asStateFlow()

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

    // Custom Playlists Flow
    private val _customPlaylists = MutableStateFlow<List<String>>(emptyList())
    val customPlaylists: StateFlow<List<String>> = _customPlaylists.asStateFlow()

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

    private val _enableBackgroundMotion = MutableStateFlow(true)
    val enableBackgroundMotion: StateFlow<Boolean> = _enableBackgroundMotion.asStateFlow()

    private val _enableGlassmorphism = MutableStateFlow(true)
    val enableGlassmorphism: StateFlow<Boolean> = _enableGlassmorphism.asStateFlow()

    private val _videoDuckingEnabled = MutableStateFlow(false)
    val videoDuckingEnabled: StateFlow<Boolean> = _videoDuckingEnabled.asStateFlow()

    private val _videoDuckingVolume = MutableStateFlow(0.2f)
    val videoDuckingVolume: StateFlow<Float> = _videoDuckingVolume.asStateFlow()

    private val _headsetControlEnabled = MutableStateFlow(true)
    val headsetControlEnabled: StateFlow<Boolean> = _headsetControlEnabled.asStateFlow()

    private val _lockscreenSwipeEnabled = MutableStateFlow(true)
    val lockscreenSwipeEnabled: StateFlow<Boolean> = _lockscreenSwipeEnabled.asStateFlow()

    private val _bgArtTransitionSpeed = MutableStateFlow(800)
    val bgArtTransitionSpeed: StateFlow<Int> = _bgArtTransitionSpeed.asStateFlow()

    fun setHeadsetControlEnabled(value: Boolean) {
        _headsetControlEnabled.value = value
        playerManager.headsetControlEnabled = value
        viewModelScope.launch {
            repository.setPreference("headset_control_enabled", value.toString())
        }
    }

    fun setLockscreenSwipeEnabled(value: Boolean) {
        _lockscreenSwipeEnabled.value = value
        playerManager.lockscreenSwipeEnabled = value
        viewModelScope.launch {
            repository.setPreference("lockscreen_swipe_enabled", value.toString())
        }
    }

    fun setBgArtTransitionSpeed(value: Int) {
        _bgArtTransitionSpeed.value = value
        viewModelScope.launch {
            repository.setPreference("bg_art_transition_speed", value.toString())
        }
    }

    fun setVideoDuckingEnabled(enabled: Boolean) {
        _videoDuckingEnabled.value = enabled
        playerManager.isVideoDuckingEnabled = enabled
        if (!enabled) {
            playerManager.isDuckedDueToVideo = false
            playerManager.mediaPlayer?.setVolume(1.0f, 1.0f)
        }
        viewModelScope.launch {
            repository.setPreference("video_ducking_enabled", enabled.toString())
        }
    }

    fun setVideoDuckingVolume(value: Float) {
        _videoDuckingVolume.value = value
        playerManager.videoDuckingVolume = value
        playerManager.updateCurrentVolumeIfNeeded()
        viewModelScope.launch {
            repository.setPreference("video_ducking_volume", value.toString())
        }
    }

    fun setEnableBackgroundMotion(enabled: Boolean) {
        _enableBackgroundMotion.value = enabled
        viewModelScope.launch {
            repository.setPreference("enable_background_motion", enabled.toString())
        }
    }

    fun setEnableGlassmorphism(enabled: Boolean) {
        _enableGlassmorphism.value = enabled
        viewModelScope.launch {
            repository.setPreference("enable_glassmorphism", enabled.toString())
        }
    }

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

        // Batch pre-load all play counts from database preferences on startup (highly efficient single query)
        viewModelScope.launch(Dispatchers.IO) {
            val allPlayPrefs = repository.getPreferencesWithPrefix("play_count_")
            val currentCounts = allPlayPrefs.associate { pref ->
                pref.key.removePrefix("play_count_") to (pref.value.toIntOrNull() ?: 0)
            }
            _playCounts.value = currentCounts
        }

        // Observe currentTrack to automatically increment play count when starting a new track.
        viewModelScope.launch(Dispatchers.IO) {
            var lastTrackId: String? = null
            playerManager.currentTrack.collect { track ->
                track?.let {
                    if (it.id != lastTrackId) {
                        lastTrackId = it.id
                        val currentCounts = _playCounts.value.toMutableMap()
                        val countVal = (currentCounts[it.id] ?: 0) + 1
                        currentCounts[it.id] = countVal
                        _playCounts.value = currentCounts
                        repository.setPreference("play_count_${it.id}", countVal.toString())
                    }
                }
            }
        }

        // Observe and forward player playback errors via Toast to prevent silent stuck issues
        viewModelScope.launch {
            playerManager.playbackError.collect { errorMsg ->
                errorMsg?.let {
                    ToastNotificationManager.showToast(
                        message = it,
                        type = ToastType.ERROR,
                        durationMs = 4000L
                    )
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
            if (currentCounts[trackId] != countVal) {
                currentCounts[trackId] = countVal
                _playCounts.value = currentCounts
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val isDark = if (themePrefs.contains("is_dark_mode")) {
                themePrefs.getBoolean("is_dark_mode", true)
            } else {
                val dbDark = repository.getThemeMode() == "dark"
                themePrefs.edit().putBoolean("is_dark_mode", dbDark).apply()
                dbDark
            }
            val folderId = repository.getGoogleFolderId()
            val userEmail = repository.getPreference("user_email").let { if (it.isNullOrEmpty()) "listener@myuloc.com" else it }
            val userName = repository.getPreference("user_name").let { if (it.isNullOrEmpty()) "MyuLoc Listener" else it }
            val userPhone = repository.getPreference("user_phone").let { if (it.isNullOrEmpty()) "" else it }
            
            val savedClientId = repository.getPreference("google_client_id")
            val clientIdVal = when {
                !savedClientId.isNullOrEmpty() -> savedClientId
                BuildConfig.GOOGLE_CLIENT_ID.isNotEmpty() && BuildConfig.GOOGLE_CLIENT_ID != "GOOGLE_CLIENT_ID_PLACEHOLDER" -> BuildConfig.GOOGLE_CLIENT_ID
                else -> ""
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

            val customThemeEnabledVal = if (themePrefs.contains("custom_theme_enabled")) {
                themePrefs.getBoolean("custom_theme_enabled", false)
            } else {
                val dbVal = repository.getPreference("custom_theme_enabled")?.toBoolean() ?: false
                themePrefs.edit().putBoolean("custom_theme_enabled", dbVal).apply()
                dbVal
            }
            val artworkThemeEnabledVal = if (themePrefs.contains("artwork_theme_enabled")) {
                themePrefs.getBoolean("artwork_theme_enabled", false)
            } else {
                val dbVal = repository.getPreference("artwork_theme_enabled")?.toBoolean() ?: false
                themePrefs.edit().putBoolean("artwork_theme_enabled", dbVal).apply()
                dbVal
            }
            val customHueVal = if (themePrefs.contains("custom_hue")) {
                themePrefs.getFloat("custom_hue", 200f)
            } else {
                val dbVal = repository.getPreference("custom_hue")?.toFloatOrNull() ?: 200f
                themePrefs.edit().putFloat("custom_hue", dbVal).apply()
                dbVal
            }
            val customSatVal = if (themePrefs.contains("custom_saturation")) {
                themePrefs.getFloat("custom_saturation", 0.80f)
            } else {
                val dbVal = repository.getPreference("custom_saturation")?.toFloatOrNull() ?: 0.80f
                themePrefs.edit().putFloat("custom_saturation", dbVal).apply()
                dbVal
            }
            val customLitVal = if (themePrefs.contains("custom_lightness")) {
                themePrefs.getFloat("custom_lightness", 0.15f)
            } else {
                val dbVal = repository.getPreference("custom_lightness")?.toFloatOrNull() ?: 0.15f
                themePrefs.edit().putFloat("custom_lightness", dbVal).apply()
                dbVal
            }

            val enableBackgroundMotionVal = repository.getPreference("enable_background_motion")?.toBoolean() ?: true
            val enableGlassmorphismVal = repository.getPreference("enable_glassmorphism")?.toBoolean() ?: true

            val videoDuckingVal = repository.getPreference("video_ducking_enabled")?.toBoolean() ?: false
            val videoDuckingVolVal = repository.getPreference("video_ducking_volume")?.toFloatOrNull() ?: 0.2f

            val headsetCtrlVal = repository.getPreference("headset_control_enabled")?.toBoolean() ?: true
            val lockscreenSwipeVal = repository.getPreference("lockscreen_swipe_enabled")?.toBoolean() ?: true
            val bgArtSpeedVal = repository.getPreference("bg_art_transition_speed")?.toIntOrNull() ?: 800

            val customPlaylistsStr = repository.getPreference("custom_playlists") ?: ""
            val customPlaylistsList = if (customPlaylistsStr.isNotEmpty()) {
                customPlaylistsStr.split(",").filter { it.isNotEmpty() }
            } else {
                emptyList()
            }

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
                _enableBackgroundMotion.value = enableBackgroundMotionVal
                _enableGlassmorphism.value = enableGlassmorphismVal
                _videoDuckingEnabled.value = videoDuckingVal
                _videoDuckingVolume.value = videoDuckingVolVal
                playerManager.isVideoDuckingEnabled = videoDuckingVal
                playerManager.videoDuckingVolume = videoDuckingVolVal

                _headsetControlEnabled.value = headsetCtrlVal
                _lockscreenSwipeEnabled.value = lockscreenSwipeVal
                _bgArtTransitionSpeed.value = bgArtSpeedVal
                playerManager.headsetControlEnabled = headsetCtrlVal
                playerManager.lockscreenSwipeEnabled = lockscreenSwipeVal

                _customThemeEnabled.value = customThemeEnabledVal
                _artworkThemeEnabled.value = artworkThemeEnabledVal
                _customHue.value = customHueVal
                _customSaturation.value = customSatVal
                _customLightness.value = customLitVal

                _customPlaylists.value = customPlaylistsList
            }
        }
    }

    fun createCustomPlaylist(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotEmpty()) {
            viewModelScope.launch {
                val current = _customPlaylists.value.toMutableList()
                if (!current.contains(trimmed)) {
                    current.add(trimmed)
                    _customPlaylists.value = current
                    withContext(Dispatchers.IO) {
                        repository.setPreference("custom_playlists", current.joinToString(","))
                    }
                }
            }
        }
    }

    fun deleteCustomPlaylist(name: String) {
        viewModelScope.launch {
            val current = _customPlaylists.value.toMutableList()
            if (current.remove(name)) {
                _customPlaylists.value = current
                withContext(Dispatchers.IO) {
                    repository.setPreference("custom_playlists", current.joinToString(","))
                }
            }
        }
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        _dynamicColorEnabled.value = enabled
        themePrefs.edit().putBoolean("dynamic_color_enabled", enabled).apply()
        if (enabled) {
            setCustomThemeEnabled(false)
            setArtworkThemeEnabled(false)
        }
    }

    fun setCustomThemeEnabled(enabled: Boolean) {
        _customThemeEnabled.value = enabled
        themePrefs.edit().putBoolean("custom_theme_enabled", enabled).apply()
        viewModelScope.launch(Dispatchers.IO) {
            repository.setPreference("custom_theme_enabled", enabled.toString())
        }
        if (enabled) {
            _dynamicColorEnabled.value = false
            themePrefs.edit().putBoolean("dynamic_color_enabled", false).apply()
        }
        if (!enabled) {
            _artworkThemeEnabled.value = false
            themePrefs.edit().putBoolean("artwork_theme_enabled", false).apply()
            viewModelScope.launch(Dispatchers.IO) {
                repository.setPreference("artwork_theme_enabled", "false")
            }
        }
    }

    fun setArtworkThemeEnabled(enabled: Boolean) {
        _artworkThemeEnabled.value = enabled
        themePrefs.edit().putBoolean("artwork_theme_enabled", enabled).apply()
        viewModelScope.launch(Dispatchers.IO) {
            repository.setPreference("artwork_theme_enabled", enabled.toString())
        }
        if (enabled) {
            _customThemeEnabled.value = true
            themePrefs.edit().putBoolean("custom_theme_enabled", true).apply()
            viewModelScope.launch(Dispatchers.IO) {
                repository.setPreference("custom_theme_enabled", "true")
            }
            _dynamicColorEnabled.value = false
            themePrefs.edit().putBoolean("dynamic_color_enabled", false).apply()
        }
    }

    fun updateThemeFromArtwork(hue: Float, saturation: Float, lightness: Float) {
        _customHue.value = hue
        _customSaturation.value = saturation
        _customLightness.value = lightness
        themePrefs.edit()
            .putFloat("custom_hue", hue)
            .putFloat("custom_saturation", saturation)
            .putFloat("custom_lightness", lightness)
            .apply()
        
        viewModelScope.launch(Dispatchers.IO) {
            repository.setPreference("custom_hue", hue.toString())
            repository.setPreference("custom_saturation", saturation.toString())
            repository.setPreference("custom_lightness", lightness.toString())
        }
    }

    fun setCustomHue(hue: Float, persist: Boolean = true) {
        _customHue.value = hue
        if (persist) {
            themePrefs.edit().putFloat("custom_hue", hue).apply()
            viewModelScope.launch(Dispatchers.IO) {
                repository.setPreference("custom_hue", hue.toString())
            }
        }
    }

    fun setCustomSaturation(sat: Float, persist: Boolean = true) {
        _customSaturation.value = sat
        if (persist) {
            themePrefs.edit().putFloat("custom_saturation", sat).apply()
            viewModelScope.launch(Dispatchers.IO) {
                repository.setPreference("custom_saturation", sat.toString())
            }
        }
    }

    fun setCustomLightness(lit: Float, persist: Boolean = true) {
        _customLightness.value = lit
        if (persist) {
            themePrefs.edit().putFloat("custom_lightness", lit).apply()
            viewModelScope.launch(Dispatchers.IO) {
                repository.setPreference("custom_lightness", lit.toString())
            }
        }
    }

    fun persistCustomThemeSettings() {
        val h = _customHue.value
        val s = _customSaturation.value
        val l = _customLightness.value
        themePrefs.edit()
            .putFloat("custom_hue", h)
            .putFloat("custom_saturation", s)
            .putFloat("custom_lightness", l)
            .apply()
        viewModelScope.launch(Dispatchers.IO) {
            repository.setPreference("custom_hue", h.toString())
            repository.setPreference("custom_saturation", s.toString())
            repository.setPreference("custom_lightness", l.toString())
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
            themePrefs.edit().putBoolean("is_dark_mode", newMode).apply()
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

    // Google Drive Fetcher (Obsolete - Offline-first only)
    fun fetchLockerFiles() {
        _lockerUiState.value = LockerUiState.Success(emptyList())
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

    private val filterConfigFlow = combine(_filterOutSmallFiles, _filterOutShortAudios) { small, short ->
        Pair(small, short)
    }

    private val sortConfigFlow = combine(_sortField, _sortDirection) { field, direction ->
        Pair(field, direction)
    }

    val offlineTracksFlow: StateFlow<List<OfflineTrack>?> = combine(
        repository.offlineTracks,
        _playCounts,
        combine(sortConfigFlow, filterConfigFlow) { sort, filter -> Pair(sort, filter) }
    ) { tracks, playCountsMap, configs ->
        val (sortConfig, filterConfig) = configs
        val (field, direction) = sortConfig
        val (filterSmall, filterShort) = filterConfig
        
        val filteredTracks = tracks.filter { track ->
            var keep = true
            if (filterShort > 0 && track.durationMs > 0 && track.durationMs < filterShort * 1000L) {
                keep = false
            }
            if (keep && filterSmall && track.localUri.startsWith("file://")) {
                val file = java.io.File(track.localUri.removePrefix("file://"))
                if (file.exists() && file.length() < 500 * 1024) {
                    keep = false
                }
            }
            keep
        }

        val sorted = when (field) {
            SortField.TITLE -> filteredTracks.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
            SortField.DATE_ADDED -> filteredTracks.sortedBy { it.timestamp }
            SortField.PLAY_COUNT -> filteredTracks.sortedBy { playCountsMap[it.id] ?: 0 }
        }
        if (direction == SortDirection.DESCENDING) {
            sorted.reversed()
        } else {
            sorted
        }
    }.flowOn(Dispatchers.IO).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- Optimized, highly efficient, background-thread StateFlows for track subsets ---

    // Pre-maps Favorite Tracks to PlayerTracks on background thread
    val favoritesPlayerTracksFlow: StateFlow<List<PlayerTrack>> = favoriteTracksFlow.map { list ->
        list.map {
            PlayerTrack(
                id = it.id,
                title = it.title,
                artist = it.artist,
                streamUrl = it.streamUrl,
                thumbnailUrl = it.thumbnailUrl,
                source = it.source,
                durationMs = it.durationMs
            )
        }
    }.flowOn(Dispatchers.Default).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pre-maps all offline tracks to PlayerTracks on background thread
    val offlinePlayerTracksFlow: StateFlow<List<PlayerTrack>> = offlineTracksFlow.map { list ->
        list?.map {
            PlayerTrack(
                id = it.id,
                title = it.title,
                artist = it.artist,
                streamUrl = it.localUri,
                thumbnailUrl = it.thumbnailUrl,
                source = it.source,
                durationMs = it.durationMs
            )
        } ?: emptyList()
    }.flowOn(Dispatchers.Default).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pre-filters Locker tracks on background thread
    val lockerPlayerTracksFlow: StateFlow<List<PlayerTrack>> = offlinePlayerTracksFlow.map { list ->
        list.filter { it.source == "Locker" }
    }.flowOn(Dispatchers.Default).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pre-filters Local device tracks on background thread
    val localPlayerTracksFlow: StateFlow<List<PlayerTrack>> = offlinePlayerTracksFlow.map { list ->
        list.filter { it.source == "Local" || it.source == "LocalScanned" }
    }.flowOn(Dispatchers.Default).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pre-filters and sorts Most Played tracks on background thread using the latest play counts
    val mostPlayedPlayerTracksFlow: StateFlow<List<PlayerTrack>> = combine(
        offlinePlayerTracksFlow,
        _playCounts
    ) { tracks, playCountsMap ->
        tracks.filter { (playCountsMap[it.id] ?: 0) > 0 }
            .sortedByDescending { playCountsMap[it.id] ?: 0 }
    }.flowOn(Dispatchers.Default).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    private val _loggedInPhone = MutableStateFlow("")
    val loggedInPhone: StateFlow<String> = _loggedInPhone.asStateFlow()

    // TODO: Admin verification must be gated server-side via a verified claim/role in the auth token.
    // Client-side hardcoded email comparison checks have been removed to prevent client-side privilege bypass.
    val isCurrentUserAdmin: StateFlow<Boolean> = _loggedInEmail.map { _ ->
        false
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

    private val streamingUrlCache = java.util.concurrent.ConcurrentHashMap<String, String>()

    // Resolve direct streaming audio URL using multiple Piped API nodes or Cobalt, with Invidious as final fallback
    private suspend fun resolveDirectStreamingUrl(videoId: String): String {
        val cached = streamingUrlCache[videoId]
        if (cached != null) {
            android.util.Log.d("MyuLocViewModel", "Stream URL cache hit for video: $videoId")
            return cached
        }
        val resolved = resolveDirectStreamingUrlInternal(videoId)
        streamingUrlCache[videoId] = resolved
        return resolved
    }

    private suspend fun resolveDirectStreamingUrlInternal(videoId: String): String {
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
                    @Suppress("DEPRECATION")
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
                    val offlineTrack = offlineMap[track.id]
                    val localUri = offlineTrack?.localUri ?: ""
                    val isReadable = if (localUri.isNotEmpty()) {
                        if (localUri.startsWith("content://")) {
                            try {
                                getApplication<android.app.Application>().contentResolver.openAssetFileDescriptor(android.net.Uri.parse(localUri), "r")?.use { true } ?: false
                            } catch (e: Exception) { false }
                        } else {
                            val path = if (localUri.startsWith("file://")) localUri.substringAfter("file://") else localUri
                            java.io.File(path).exists() && java.io.File(path).canRead()
                        }
                    } else false

                    if (isReadable) {
                        track.copy(streamUrl = localUri)
                    } else {
                        track.copy(streamUrl = "")
                    }
                } else {
                    track
                }

                // Resolve entire list's download status to play fluidly even offline instantly using lookups in O(1) lookup time
                val updatedQueue = queueInput.map { qTrack ->
                    val cachedOffline = offlineMap[qTrack.id]
                    if (cachedOffline != null) {
                        val localUri = cachedOffline.localUri
                        val isReadable = if (localUri.isNotEmpty()) {
                            if (localUri.startsWith("content://")) {
                                try {
                                    getApplication<android.app.Application>().contentResolver.openAssetFileDescriptor(android.net.Uri.parse(localUri), "r")?.use { true } ?: false
                                } catch (e: Exception) { false }
                            } else {
                                val path = if (localUri.startsWith("file://")) localUri.substringAfter("file://") else localUri
                                java.io.File(path).exists() && java.io.File(path).canRead()
                            }
                        } else false
                        if (isReadable) qTrack.copy(streamUrl = localUri) else qTrack.copy(streamUrl = "")
                    } else {
                        qTrack
                    }
                }

                // If streamUrl is empty (even if marked Locker/Offline, e.g. due to database file deletion, mismatch, or unset value), 
                // we resolve the stream from online sources dynamically so it NEVER fails to select and play!
                if (playableTrack.streamUrl.isEmpty()) {
                    val resolvedUrl = resolveDirectStreamingUrl(playableTrack.id)
                    val fullTrack = playableTrack.copy(streamUrl = resolvedUrl)
                    val finalQueue = updatedQueue.map {
                        if (it.id == playableTrack.id) fullTrack else it.copy(streamUrl = if (it.id == playableTrack.id) resolvedUrl else it.streamUrl)
                    }
                    playerManager.playTrack(fullTrack, finalQueue)
                } else {
                    // Instantly play
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
                ToastNotificationManager.showToast(
                    message = "Connect to Google Drive to download.",
                    type = ToastType.WARNING
                )
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
                            ToastNotificationManager.showToast(
                                message = "Downloaded: ${track.title}",
                                type = ToastType.SUCCESS
                            )
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            ToastNotificationManager.showToast(
                                message = "Download Error ${responseCode}. Connect/Verify Drive connection.",
                                type = ToastType.ERROR
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    ToastNotificationManager.showToast(
                        message = "Download failed: ${e.localizedMessage}",
                        type = ToastType.ERROR
                    )
                }
            } finally {
                _downloadProgress.value = _downloadProgress.value - track.id
            }
        }
    }

    // Download entire visible locker folder contents
    fun downloadAllLockerTracks(tracks: List<PlayerTrack>) {
        if (tracks.isEmpty()) return
        ToastNotificationManager.showToast(
            message = "Preparing background downloads for ${tracks.size} tracks...",
            type = ToastType.INFO
        )
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

    private fun purgeLocalFileFromSystem(localUriStr: String) {
        try {
            val contentResolver = getApplication<Application>().contentResolver
            if (localUriStr.startsWith("content://")) {
                val uri = android.net.Uri.parse(localUriStr)
                
                // Try resolving physical path from cursor first
                var resolvedPath: String? = null
                try {
                    contentResolver.query(uri, arrayOf(android.provider.MediaStore.Audio.Media.DATA), null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val index = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA)
                            if (index != -1) {
                                resolvedPath = cursor.getString(index)
                            }
                        }
                    }
                } catch (ignored: Exception) {}
                
                if (resolvedPath != null) {
                    val f = java.io.File(resolvedPath)
                    if (f.exists()) {
                        f.delete()
                    }
                }
                
                // Delete via content resolver (deletes from ContentProvider & storage physically)
                contentResolver.delete(uri, null, null)
            } else {
                val path = if (localUriStr.startsWith("file://")) localUriStr.substringAfter("file://") else localUriStr
                val file = java.io.File(path)
                
                // Deletion from MediaStore
                try {
                    val mediaUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    val selection = "${android.provider.MediaStore.Audio.Media.DATA} = ?"
                    val selectionArgs = arrayOf(path)
                    contentResolver.delete(mediaUri, selection, selectionArgs)
                } catch (ignored: Exception) {}
                
                // Delete physical file
                if (file.exists()) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteSelectedTracks() {
        val idsToDelete = _selectedDeleteIds.value.toList()
        viewModelScope.launch(Dispatchers.IO) {
            idsToDelete.forEach { trackId ->
                val offline = repository.getOfflineTrack(trackId)
                if (offline != null) {
                    // Skip if currently playing
                    val currentTrack = playerManager.currentTrack.value
                    if (currentTrack?.id == trackId) {
                        withContext(Dispatchers.Main) {
                            playerManager.skipNext()
                        }
                    }
                    // Remove from playback queue
                    withContext(Dispatchers.Main) {
                        playerManager.removeFromQueue(trackId)
                    }

                    purgeLocalFileFromSystem(offline.localUri)
                    repository.removeOfflineTrack(trackId)
                }
            }
            withContext(Dispatchers.Main) {
                _selectedDeleteIds.value = emptySet()
                _isMultiSelectMode.value = false
                ToastNotificationManager.showToast(
                    message = "Successfully deleted ${idsToDelete.size} tracks from device.",
                    type = ToastType.SUCCESS
                )
            }
        }
    }

    // Remove local download / path
    fun deleteOfflineTrack(trackId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val offline = repository.getOfflineTrack(trackId)
            if (offline != null) {
                // Skip if currently playing
                val currentTrack = playerManager.currentTrack.value
                if (currentTrack?.id == trackId) {
                    withContext(Dispatchers.Main) {
                        playerManager.skipNext()
                    }
                }
                // Remove from playback queue
                withContext(Dispatchers.Main) {
                    playerManager.removeFromQueue(trackId)
                }

                purgeLocalFileFromSystem(offline.localUri)
                repository.removeOfflineTrack(trackId)
                withContext(Dispatchers.Main) {
                    ToastNotificationManager.showToast(
                        message = "Deleted: ${offline.title}",
                        type = ToastType.SUCCESS
                    )
                }
            }
        }
    }

    // Scan standard device directories for audio files using MediaStore (and legacy File walker as fallback)
    fun scanLocalFiles(force: Boolean = false) {
        val prefs = getApplication<Application>().getSharedPreferences("myuloc_prefs", Context.MODE_PRIVATE)
        if (!force && prefs.getBoolean("has_scanned_local", false)) {
            return
        }
        
        // Save immediately to prevent endless scan loops on every startup if interrupted or slow
        prefs.edit().putBoolean("has_scanned_local", true).apply()
        
        viewModelScope.launch {
            try {
                ToastNotificationManager.showToast(
                    message = "Scanning device storage for local songs...",
                    type = ToastType.INFO
                )
                val list = mutableListOf<OfflineTrack>()
                
                // 0. Always scan the app's internal sandboxed "downloads" folder first (highest priority for private appdata)
                withContext(Dispatchers.IO) {
                    try {
                        val downloadsDir = java.io.File(getApplication<Application>().filesDir, "downloads")
                        if (downloadsDir.exists() && downloadsDir.isDirectory) {
                            scanDirectoryForAudio(downloadsDir, list)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
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
                            android.provider.MediaStore.Audio.Media.DATA,
                            android.provider.MediaStore.Audio.Media.ALBUM_ID
                        )
                        // Query all music files
                        val selection = "${android.provider.MediaStore.Audio.Media.IS_MUSIC} != 0"
                        contentResolver.query(uri, projection, selection, null, null)?.use { cursor ->
                            val idCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media._ID)
                            val titleCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.TITLE)
                            val artistCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.ARTIST)
                            val durationCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DURATION)
                            val dataCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA)
                            val albumIdCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.ALBUM_ID)
                            
                            while (cursor.moveToNext()) {
                                val path = cursor.getString(dataCol)
                                if (path.isNullOrEmpty()) continue
                                
                                val duration = cursor.getLong(durationCol)

                                val title = cursor.getString(titleCol) ?: "Unknown Track"
                                val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                                val albumId = cursor.getLong(albumIdCol)
                                val albumArtUri = "content://media/external/audio/albumart/$albumId"
                                val id = "local_${path.hashCode()}"
                                list.add(
                                    OfflineTrack(
                                        id = id,
                                        title = title,
                                        artist = artist,
                                        durationMs = duration,
                                        source = "LocalScanned",
                                        localUri = "file://$path",
                                        thumbnailUrl = albumArtUri
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // 2. Legacy direct directories scan fallback (Removed to prevent massive lag on devices with large Downloads folders. MediaStore covers this).
                
                withContext(Dispatchers.IO) {
                    repository.addOfflineTracks(list)
                }

               withContext(Dispatchers.Main) {
                    ToastNotificationManager.showToast(
                        message = "Scan complete. Discovered and cataloged ${list.size} music tracks.",
                        type = ToastType.SUCCESS,
                        durationMs = 4500L
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    ToastNotificationManager.showToast(
                        message = "Scan failed or interrupted: ${e.localizedMessage}",
                        type = ToastType.ERROR,
                        durationMs = 4500L
                    )
                }
            }
        }
    }

    private fun scanDirectoryForAudio(dir: java.io.File, list: MutableList<OfflineTrack>) {
        if (!dir.exists() || !dir.isDirectory) return
        val files = dir.listFiles() ?: return
        val mmr = android.media.MediaMetadataRetriever()
        for (file in files) {
            if (file.isDirectory) {
                scanDirectoryForAudio(file, list)
            } else {
                val name = file.name.lowercase()
                if (name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".flac") || name.endsWith(".m4a") || name.endsWith(".ogg")) {
                    val parts = splitArtistTitle(file.name)
                    val id = "local_${file.absolutePath.hashCode()}"
                    
                    var thumbUrl = ""
                    var parsedTitle = parts.second
                    var parsedArtist = parts.first
                    var parsedDuration = 240000L
                    try {
                        mmr.setDataSource(file.absolutePath)
                        val metaTitle = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_TITLE)
                        val metaArtist = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST)
                        val metaDuration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
                        if (!metaTitle.isNullOrEmpty()) parsedTitle = metaTitle
                        if (!metaArtist.isNullOrEmpty()) parsedArtist = metaArtist
                        if (!metaDuration.isNullOrEmpty()) {
                            parsedDuration = metaDuration.toLongOrNull() ?: parsedDuration
                        }
                        val picture = mmr.embeddedPicture
                        if (picture != null) {
                            val artFile = java.io.File(getApplication<android.app.Application>().cacheDir, "art_${file.name.hashCode()}.jpg")
                            if (!artFile.exists()) {
                                artFile.writeBytes(picture)
                            }
                            thumbUrl = "file://${artFile.absolutePath}"
                        }
                    } catch (e: Exception) {
                        // Ignore MMR failures
                    }

                    list.add(
                        OfflineTrack(
                            id = id,
                            title = parsedTitle,
                            artist = parsedArtist,
                            durationMs = parsedDuration,
                            source = "Local",
                            localUri = "file://${file.absolutePath}",
                            thumbnailUrl = thumbUrl
                        )
                    )
                }
            }
        }
        try { mmr.release() } catch(e: Exception){}
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
            _isUserLoggedIn.value = false
            _loggedInEmail.value = "listener@myuloc.com"
            _loggedInName.value = "MyuLoc Listener"
            _loggedInPhone.value = ""
            repository.setPreference("user_logged_in", "false")
            repository.setPreference("user_email", "listener@myuloc.com")
            repository.setPreference("user_name", "MyuLoc Listener")
            repository.setPreference("user_phone", "")
            Toast.makeText(getApplication(), "Signed out successfully", Toast.LENGTH_SHORT).show()
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

    // Share Track Action (Standard Android Share Sheet supporting physical audio files and song links/details)
    fun shareTrack(context: android.content.Context, track: PlayerTrack) {
        viewModelScope.launch(Dispatchers.IO) {
            val offline = repository.getOfflineTrack(track.id)
            val fileToShare: java.io.File? = if (offline != null) {
                val localUriStr = offline.localUri
                val path = if (localUriStr.startsWith("file://")) localUriStr.substringAfter("file://") else localUriStr
                val f = java.io.File(path)
                if (f.exists()) f else null
            } else null

            withContext(Dispatchers.Main) {
                try {
                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        if (fileToShare != null) {
                            // Share the actual audio file via registered FileProvider
                            val fileUri = androidx.core.content.FileProvider.getUriForFile(
                                context,
                                "com.example.fileprovider",
                                fileToShare
                            )
                            type = "audio/*"
                            putExtra(android.content.Intent.EXTRA_STREAM, fileUri)
                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Sharing Audio: ${track.title}")
                            putExtra(android.content.Intent.EXTRA_TEXT, "Listen to '${track.title}' by ${track.artist} on MyuLoc!")
                        } else {
                            // Share as plain text info
                            type = "text/plain"
                            val shareMessage = "Listen to '${track.title}' by ${track.artist} on MyuLoc!\n(Source: ${track.source})"
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Sharing Song Info: ${track.title}")
                            putExtra(android.content.Intent.EXTRA_TEXT, shareMessage)
                        }
                    }
                    val chooser = android.content.Intent.createChooser(intent, "Share Track via")
                    chooser.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(chooser)
                } catch (e: Exception) {
                    android.util.Log.e("MyuLocViewModel", "Error sharing track: ${e.message}")
                    Toast.makeText(context, "Could not share track: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun fetchDriveStorage() {
        // Return local sandboxed space estimation (Offline Mode)
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
    }

    // Load trending or beautiful popular proxy streams to present on initial Search screen
    fun loadProxyHotlist() {
        _proxyHotlist.value = emptyList()
        _isFetchingHotlist.value = false
    }

    // Ping test Invidious custom proxy endpoint and return status logs for admins
    fun verifyInvidiousNode(url: String, callback: (String) -> Unit) {
        callback("System is operating fully in high-performance local/offline state.\nOnline API proxy engines have been deprecated and removed.")
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

    // --- Simple, direct methods for music selection and play-count management ---

    /**
     * Instantly select and play a track via a simple, direct method.
     */
    fun selectAndPlayTrack(track: OfflineTrack) {
        val playerTrack = PlayerTrack(
            id = track.id,
            title = track.title,
            artist = track.artist,
            streamUrl = track.localUri,
            thumbnailUrl = track.thumbnailUrl,
            source = track.source,
            durationMs = track.durationMs
        )
        playerManager.playTrack(playerTrack)
    }

    /**
     * Retrieve the complete list of all selectable offline tracks as a simple, plain list.
     */
    fun getAllSelectableTracks(): List<OfflineTrack> {
        return offlineTracksFlow.value ?: emptyList()
    }

    /**
     * Get the play count for a given track using a simple synchronous getter.
     */
    fun getTrackPlayCount(trackId: String): Int {
        return _playCounts.value[trackId] ?: 0
    }

    /**
     * Manually increment a track's play count with a simple, direct method.
     */
    fun incrementTrackPlayCount(trackId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentCounts = _playCounts.value.toMutableMap()
            val newCount = (currentCounts[trackId] ?: 0) + 1
            currentCounts[trackId] = newCount
            _playCounts.value = currentCounts
            repository.setPreference("play_count_$trackId", newCount.toString())
        }
    }

    /**
     * Reset a track's play count back to zero using a simple, direct method.
     */
    fun resetTrackPlayCount(trackId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentCounts = _playCounts.value.toMutableMap()
            currentCounts[trackId] = 0
            _playCounts.value = currentCounts
            repository.setPreference("play_count_$trackId", "0")
        }
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

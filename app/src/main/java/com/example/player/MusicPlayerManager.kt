package com.example.player

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class PlaybackRepeatMode { OFF, ONE, ALL }

data class PlayerTrack(
    val id: String,
    val title: String,
    val artist: String,
    val streamUrl: String,
    val thumbnailUrl: String,
    val source: String, // "Locker", "Search", or "Demo"
    val durationMs: Long = 0L
)

fun serializeQueue(queue: List<PlayerTrack>): String {
    return queue.joinToString(separator = "\u001e") { track ->
        "${track.id}\u001f${track.title.replace("\u001f"," ").replace("\u001e"," ")}\u001f${track.artist.replace("\u001f"," ").replace("\u001e"," ")}\u001f${track.streamUrl}\u001f${track.thumbnailUrl}\u001f${track.source}\u001f${track.durationMs}"
    }
}

fun deserializeQueue(serialized: String): List<PlayerTrack> {
    if (serialized.isEmpty()) return emptyList()
    return try {
        serialized.split("\u001e").mapNotNull { item ->
            val parts = item.split("\u001f")
            if (parts.size >= 7) {
                PlayerTrack(
                    id = parts[0],
                    title = parts[1],
                    artist = parts[2],
                    streamUrl = parts[3],
                    thumbnailUrl = parts[4],
                    source = parts[5],
                    durationMs = parts[6].toLongOrNull() ?: 0L
                )
            } else null
        }
    } catch (e: Exception) {
        emptyList()
    }
}

class MusicPlayerManager private constructor(private val context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: MusicPlayerManager? = null

        fun getInstance(context: Context): MusicPlayerManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MusicPlayerManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        val instance: MusicPlayerManager?
            get() = INSTANCE
    }

    private fun runOnMainThread(action: () -> Unit) {
        if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
            action()
        } else {
            android.os.Handler(android.os.Looper.getMainLooper()).post(action)
        }
    }

    var mediaPlayer: android.media.MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null
    private var wifiLock: android.net.wifi.WifiManager.WifiLock? = null
    private var wakeLock: android.os.PowerManager.WakeLock? = null
    private var noisyReceiver: android.content.BroadcastReceiver? = null
    private var consecutiveValidationFailures = 0
    private val audioManager: android.media.AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
    }
    private var audioFocusRequest: android.media.AudioFocusRequest? = null
    private var playOnFocusGain = false
    
    // Smart video ducking configuration properties
    var isVideoDuckingEnabled = false
    var videoDuckingVolume = 0.2f
    var isDuckedDueToVideo = false
    var headsetControlEnabled = true
    var lockscreenSwipeEnabled = true

    fun updateCurrentVolumeIfNeeded() {
        if (isDuckedDueToVideo) {
            mediaPlayer?.setVolume(videoDuckingVolume, videoDuckingVolume)
        }
    }
    
    private val audioFocusChangeListener = android.media.AudioManager.OnAudioFocusChangeListener { focusChange ->
        runOnMainThread {
            val isInCall = audioManager.mode == android.media.AudioManager.MODE_IN_CALL || 
                           audioManager.mode == android.media.AudioManager.MODE_IN_COMMUNICATION
            
            when (focusChange) {
                android.media.AudioManager.AUDIOFOCUS_GAIN -> {
                    isDuckedDueToVideo = false
                    if (playOnFocusGain) {
                        try {
                            mediaPlayer?.start()
                            _isPlaying.value = true
                            _isAudioDeliveringSound.value = true
                            startProgressTracker()
                            acquireLocks()
                            playOnFocusGain = false
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                    mediaPlayer?.setVolume(1.0f, 1.0f)
                }
                android.media.AudioManager.AUDIOFOCUS_LOSS -> {
                    if (isVideoDuckingEnabled && !isInCall) {
                        isDuckedDueToVideo = true
                        mediaPlayer?.setVolume(videoDuckingVolume, videoDuckingVolume)
                    } else {
                        isDuckedDueToVideo = false
                        pausePlayback(releaseFocus = true)
                    }
                }
                android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    if (isVideoDuckingEnabled && !isInCall) {
                        isDuckedDueToVideo = true
                        mediaPlayer?.setVolume(videoDuckingVolume, videoDuckingVolume)
                    } else {
                        isDuckedDueToVideo = false
                        pausePlayback(releaseFocus = false)
                        playOnFocusGain = true
                    }
                }
                android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    isDuckedDueToVideo = true
                    mediaPlayer?.setVolume(videoDuckingVolume, videoDuckingVolume)
                }
            }
        }
    }

    // State flows
    private val _currentTrack = MutableStateFlow<PlayerTrack?>(null)
    val currentTrack: StateFlow<PlayerTrack?> = _currentTrack

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering

    private val _manualSeekEvents = kotlinx.coroutines.flow.MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val manualSeekEvents: kotlinx.coroutines.flow.SharedFlow<Long> = _manualSeekEvents

    private val _isAudioDeliveringSound = MutableStateFlow(false)
    val isAudioDeliveringSound: StateFlow<Boolean> = _isAudioDeliveringSound

    private val _playbackError = MutableStateFlow<String?>(null)
    val playbackError: StateFlow<String?> = _playbackError

    // Queue management
    private val _queue = MutableStateFlow<List<PlayerTrack>>(emptyList())
    val queue: StateFlow<List<PlayerTrack>> = _queue

    private var currentIndex = -1
    private var googleAccessToken: String? = null
    private var googleApiKey: String? = null

    // Equalizer variables
    private val equalizerLock = Any()
    private var equalizer: android.media.audiofx.Equalizer? = null
    val eqBands = MutableStateFlow(listOf(0, 0, 0, 0, 0))
    val eqEnabled = MutableStateFlow(false)

    enum class MyuLocShuffleMode {
        OFF,
        STANDARD,
        DYNAMIC
    }

    // Shuffle and Repeat states
    val shuffleMode = MutableStateFlow(MyuLocShuffleMode.OFF)
    val shuffleEnabled = MutableStateFlow(false)
    val repeatMode = MutableStateFlow(PlaybackRepeatMode.OFF)
    val shuffleInterval = MutableStateFlow(5)
    var songsPlayedInBlock = 1

    init {
        initializePlayer()
        
        scope.launch {
            try {
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                    
                    val savedShuffleModeStr = db.preferenceDao().getPreference("shuffle_mode") ?: "OFF"
                    val savedShuffleMode = try {
                        MyuLocShuffleMode.valueOf(savedShuffleModeStr)
                    } catch (e: Exception) {
                        val savedShuffle = db.preferenceDao().getPreference("shuffle_enabled")?.toBoolean() ?: false
                        if (savedShuffle) MyuLocShuffleMode.STANDARD else MyuLocShuffleMode.OFF
                    }
                    shuffleMode.value = savedShuffleMode
                    shuffleEnabled.value = (savedShuffleMode != MyuLocShuffleMode.OFF)

                    val savedInterval = db.preferenceDao().getPreference("shuffle_interval")?.toIntOrNull() ?: 5
                    shuffleInterval.value = savedInterval.coerceIn(1, 30)
                    
                    val savedEqEnabled = db.preferenceDao().getPreference("eq_enabled")?.toBoolean() ?: false
                    eqEnabled.value = savedEqEnabled
                    
                    val savedEqBands = db.preferenceDao().getPreference("eq_bands")
                    if (savedEqBands != null) {
                        try {
                            val bands = savedEqBands.split(",").map { it.toInt() }
                            if (bands.size == 5) {
                                eqBands.value = bands
                            }
                        } catch (e: Exception) { }
                    }
                    
                    val savedRepeat = db.preferenceDao().getPreference("repeat_mode") ?: "OFF"
                    repeatMode.value = try {
                        PlaybackRepeatMode.valueOf(savedRepeat)
                    } catch (e: Exception) {
                        PlaybackRepeatMode.OFF
                    }

                    // Restore last played track reference
                    val trackId = db.preferenceDao().getPreference("last_track_id")
                    if (!trackId.isNullOrEmpty()) {
                        val title = db.preferenceDao().getPreference("last_track_title") ?: ""
                        val artist = db.preferenceDao().getPreference("last_track_artist") ?: ""
                        val streamUrl = db.preferenceDao().getPreference("last_track_streamUrl") ?: ""
                        val thumbnailUrl = db.preferenceDao().getPreference("last_track_thumbnailUrl") ?: ""
                        val source = db.preferenceDao().getPreference("last_track_source") ?: "Demo"
                        val durationMs = db.preferenceDao().getPreference("last_track_durationMs")?.toLongOrNull() ?: 0L
                        val savedPos = db.preferenceDao().getPreference("last_track_positionMs")?.toLongOrNull() ?: 0L
                        
                        val restoredTrack = PlayerTrack(
                            id = trackId,
                            title = title,
                            artist = artist,
                            streamUrl = streamUrl,
                            thumbnailUrl = thumbnailUrl,
                            source = source,
                            durationMs = durationMs
                        )
                        
                        val savedQueueStr = db.preferenceDao().getPreference("last_queue") ?: ""
                        val restoredQueue = deserializeQueue(savedQueueStr)
                        
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            _currentTrack.value = restoredTrack
                            _currentPosition.value = savedPos
                            _duration.value = durationMs
                            if (restoredQueue.isNotEmpty()) {
                                val idx = restoredQueue.indexOfFirst { it.id == trackId }
                                if (idx != -1) {
                                    currentIndex = idx
                                    _queue.value = restoredQueue
                                } else {
                                    val mutableQueue = restoredQueue.toMutableList()
                                    mutableQueue.add(restoredTrack)
                                    currentIndex = mutableQueue.lastIndex
                                    _queue.value = mutableQueue
                                }
                            } else {
                                currentIndex = 0
                                _queue.value = listOf(restoredTrack)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Persistence observer for shuffle mode state
            launch(kotlinx.coroutines.Dispatchers.IO) {
                shuffleMode.collect { mode ->
                    try {
                        val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                        db.preferenceDao().setPreference(
                            com.example.data.database.PreferenceEntity("shuffle_mode", mode.name)
                        )
                        db.preferenceDao().setPreference(
                            com.example.data.database.PreferenceEntity("shuffle_enabled", (mode != MyuLocShuffleMode.OFF).toString())
                        )
                        shuffleEnabled.value = (mode != MyuLocShuffleMode.OFF)
                        if (mode != MyuLocShuffleMode.OFF) {
                            runOnMainThread {
                                reShuffleRemainingQueue()
                                songsPlayedInBlock = 1
                            }
                        } else {
                            runOnMainThread {
                                sortQueueByTitle(ascending = true)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // Persistence observer for shuffle interval slider state
            launch(kotlinx.coroutines.Dispatchers.IO) {
                shuffleInterval.collect { value ->
                    try {
                        val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                        db.preferenceDao().setPreference(
                            com.example.data.database.PreferenceEntity("shuffle_interval", value.toString())
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // Persistence observer for repeat mode state
            launch(kotlinx.coroutines.Dispatchers.IO) {
                repeatMode.collect { mode ->
                    try {
                        val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                        db.preferenceDao().setPreference(
                            com.example.data.database.PreferenceEntity("repeat_mode", mode.name)
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // Persist queue updates to SQLite background storage
            launch(kotlinx.coroutines.Dispatchers.IO) {
                _queue.collect { list ->
                    if (list.isNotEmpty()) {
                        try {
                            val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                            val serialized = serializeQueue(list)
                            db.preferenceDao().setPreference(com.example.data.database.PreferenceEntity("last_queue", serialized))
                            db.preferenceDao().setPreference(com.example.data.database.PreferenceEntity("last_track_index", currentIndex.toString()))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun initializePlayer() {
        runOnMainThread {
            if (mediaPlayer != null) return@runOnMainThread
            try {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
                wifiLock = wifiManager.createWifiLock(android.net.wifi.WifiManager.WIFI_MODE_FULL, "MyuLoc:WifiLock")
            } catch (e: Exception) { e.printStackTrace() }
            
            try {
                val powerManager = context.applicationContext.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
                wakeLock = powerManager.newWakeLock(android.os.PowerManager.PARTIAL_WAKE_LOCK, "MyuLoc:WakeLock")
            } catch (e: Exception) { e.printStackTrace() }

            mediaPlayer = android.media.MediaPlayer().apply {
                setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setOnPreparedListener { mp ->
                    _isBuffering.value = false
                    _duration.value = mp.duration.toLong()
                    if (requestAudioFocus()) {
                        isDuckedDueToVideo = false
                        mp.setVolume(1.0f, 1.0f)
                        mp.start()
                        _isPlaying.value = true
                        _isAudioDeliveringSound.value = true
                        startProgressTracker()
                        acquireLocks()
                    } else {
                        _isPlaying.value = false
                        _isAudioDeliveringSound.value = false
                    }
                    maybeInitEqualizer()
                    try {
                        val intent = android.content.Intent(context, MyuLocPlaybackService::class.java)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }
                    } catch (e: Throwable) { e.printStackTrace() }
                }
                setOnCompletionListener {
                    handleTrackEnded()
                }
                setOnErrorListener { _, what, extra ->
                    _playbackError.value = "MediaPlayer playback error: code $what, extra $extra"
                    _isBuffering.value = false
                    _isPlaying.value = false
                    _isAudioDeliveringSound.value = false
                    false
                }
                setOnInfoListener { _, what, extra ->
                    try {
                        if (what == android.media.MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            _isBuffering.value = true
                            _isAudioDeliveringSound.value = false
                        } else if (what == android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            _isBuffering.value = false
                            if (_isPlaying.value) {
                                _isAudioDeliveringSound.value = true
                            }
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                    false
                }
            }
            registerNoisyReceiver()
        }
    }

    private fun registerNoisyReceiver() {
        if (noisyReceiver == null) {
            val receiver = object : android.content.BroadcastReceiver() {
                override fun onReceive(context: android.content.Context, intent: android.content.Intent) {
                    if (intent.action == android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                        runOnMainThread {
                            try {
                                if (isPlaying.value) {
                                    togglePlayPause()
                                }
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                    }
                }
            }
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    context.registerReceiver(
                        receiver,
                        android.content.IntentFilter(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY),
                        android.content.Context.RECEIVER_NOT_EXPORTED
                    )
                } else {
                    context.registerReceiver(
                        receiver,
                        android.content.IntentFilter(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                    )
                }
                noisyReceiver = receiver
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun maybeInitEqualizer() {
        try {
            val mp = mediaPlayer ?: return
            val session = mp.audioSessionId
            if (session != android.media.AudioManager.AUDIO_SESSION_ID_GENERATE) {
                reinitEqualizer(session)
            }
        } catch (e: Throwable) { e.printStackTrace() }
    }

    private fun isEmulator(): Boolean {
        val fm = android.os.Build.FINGERPRINT ?: ""
        val model = android.os.Build.MODEL ?: ""
        val manuf = android.os.Build.MANUFACTURER ?: ""
        val hardware = android.os.Build.HARDWARE ?: ""
        val product = android.os.Build.PRODUCT ?: ""
        val brand = android.os.Build.BRAND ?: ""
        val device = android.os.Build.DEVICE ?: ""
        
        val isMatch = fm.startsWith("generic") ||
                fm.startsWith("unknown") ||
                fm.contains("emulator") ||
                fm.contains("simulator") ||
                model.contains("google_sdk") ||
                model.contains("Emulator") ||
                model.contains("Android SDK built for x86") ||
                model.contains("gphone") ||
                model.contains("sdk_gphone") ||
                manuf.contains("Genymotion") ||
                hardware.contains("goldfish") ||
                hardware.contains("ranchu") ||
                hardware.contains("vsoc") ||
                hardware.contains("cutf") ||
                hardware.contains("cuttlefish") ||
                (brand.startsWith("generic") && device.startsWith("generic")) ||
                ("google" == brand && product.contains("sdk_gphone")) ||
                product.contains("sdk_google") ||
                product.contains("vbox86p") ||
                product.contains("emulator") ||
                product.contains("simulator")
        return isMatch
    }

    private fun reinitEqualizer(sessionId: Int) {
        if (isEmulator()) {
            android.util.Log.d("MusicPlayerManager", "Running in simulator. Skipping physical equalizer instance to avoid native crash.")
            return
        }
        synchronized(equalizerLock) {
            try {
                equalizer?.release()
                equalizer = null
            } catch (e: Throwable) { e.printStackTrace() }

            try {
                equalizer = android.media.audiofx.Equalizer(0, sessionId).apply {
                    enabled = eqEnabled.value
                    val bandsToSet = eqBands.value
                    for (bandIndex in bandsToSet.indices) {
                        if (bandIndex < numberOfBands) {
                            val level = bandsToSet[bandIndex].coerceIn(
                                bandLevelRange[0].toInt(),
                                bandLevelRange[1].toInt()
                            )
                            setBandLevel(bandIndex.toShort(), level.toShort())
                        }
                    }
                }
            } catch (e: Throwable) {
                equalizer = null
                android.util.Log.d("MusicPlayerManager", "Equalizer effect is not supported by device hardware. Skipping.")
            }
        }
    }

    private fun saveEqSettings() {
        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                db.preferenceDao().setPreference(
                    com.example.data.database.PreferenceEntity("eq_enabled", eqEnabled.value.toString())
                )
                db.preferenceDao().setPreference(
                    com.example.data.database.PreferenceEntity("eq_bands", eqBands.value.joinToString(","))
                )
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun setEqEnabled(enabled: Boolean) {
        eqEnabled.value = enabled
        synchronized(equalizerLock) {
            try {
                equalizer?.enabled = enabled
            } catch (e: Throwable) { e.printStackTrace() }
        }
        saveEqSettings()
    }

    fun setEqBandLevel(band: Int, levelMilliBels: Int) {
        val currentLevels = eqBands.value.toMutableList()
        if (band in currentLevels.indices) {
            currentLevels[band] = levelMilliBels
            eqBands.value = currentLevels
            synchronized(equalizerLock) {
                try {
                    equalizer?.let { eq ->
                        if (band < eq.numberOfBands) {
                            eq.setBandLevel(band.toShort(), levelMilliBels.toShort())
                        }
                    }
                } catch (e: Throwable) { e.printStackTrace() }
            }
            saveEqSettings()
        }
    }

    fun addToQueue(track: PlayerTrack) {
        val currentList = _queue.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == track.id }
        if (index == -1) {
            currentList.add(track)
            _queue.value = currentList
            if (currentIndex == -1) {
                currentIndex = 0
                _currentTrack.value = track
                prepareAndPlay(track)
            }
        }
    }

    fun removeFromQueue(trackId: String) {
        val currentList = _queue.value.toMutableList()
        val idx = currentList.indexOfFirst { it.id == trackId }
        if (idx != -1) {
            currentList.removeAt(idx)
            _queue.value = currentList
            if (currentIndex == idx) {
                if (currentList.isEmpty()) {
                    clearQueue()
                } else {
                    val nextIdx = idx % currentList.size
                    currentIndex = nextIdx
                    val nextTrack = currentList[nextIdx]
                    _currentTrack.value = nextTrack
                    prepareAndPlay(nextTrack)
                }
            } else if (currentIndex > idx) {
                currentIndex--
            }
        }
    }

    fun clearQueue() {
        _queue.value = emptyList()
        currentIndex = -1
        _currentTrack.value = null
        try {
            mediaPlayer?.stop()
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun setGoogleAccessToken(token: String?) {
        googleAccessToken = token
    }

    fun setGoogleApiKey(key: String?) {
        googleApiKey = key
    }

    fun playGenreMix(mood: String, genreGroups: Map<String, List<PlayerTrack>>) {
        runOnMainThread {
            val mixedTracks = mutableListOf<PlayerTrack>()
            val targetGenres = if (mood == "Chill") {
                listOf("Chill & Lofi", "Acoustic & Classical")
            } else {
                listOf("Electronic & Dance", "Pop, Rock & Indie", "Hip-Hop & R&B")
            }
            
            targetGenres.forEach { genre ->
                genreGroups[genre]?.let { mixedTracks.addAll(it) }
            }
            
            if (mixedTracks.isNotEmpty()) {
                val shuffled = mixedTracks.shuffled()
                playTrack(shuffled[0], shuffled)
            }
        }
    }

    fun playRandomGenre(genreGroups: Map<String, List<PlayerTrack>>) {
        runOnMainThread {
            if (genreGroups.isEmpty()) return@runOnMainThread
            val randomGenre = genreGroups.keys.random()
            val tracks = genreGroups[randomGenre]?.shuffled() ?: emptyList()
            if (tracks.isNotEmpty()) {
                playTrack(tracks[0], tracks)
            }
        }
    }

    fun playGenreJumping(genreGroups: Map<String, List<PlayerTrack>>) {
        runOnMainThread {
            if (genreGroups.isEmpty()) return@runOnMainThread
            val jumpingQueue = mutableListOf<PlayerTrack>()
            val keys = genreGroups.keys.shuffled()
            keys.forEach { genre ->
                val tracks = genreGroups[genre]?.shuffled()?.take(2) ?: emptyList()
                jumpingQueue.addAll(tracks)
            }
            
            if (jumpingQueue.isNotEmpty()) {
                playTrack(jumpingQueue[0], jumpingQueue)
            }
        }
    }

    fun playTrack(track: PlayerTrack, newQueue: List<PlayerTrack> = emptyList()) {
        val qInput = if (newQueue.isNotEmpty()) newQueue else _queue.value
        
        if (shuffleMode.value != MyuLocShuffleMode.OFF) {
            val otherTracks = qInput.filter { it.id != track.id }.shuffled()
            val finalQueue = listOf(track) + otherTracks
            currentIndex = 0
            _queue.value = finalQueue
            songsPlayedInBlock = 1
        } else {
            if (newQueue.isNotEmpty()) {
                currentIndex = newQueue.indexOfFirst { it.id == track.id }
                _queue.value = newQueue
            } else {
                val idx = _queue.value.indexOfFirst { it.id == track.id }
                if (idx != -1) {
                    currentIndex = idx
                } else {
                    val updated = _queue.value + track
                    currentIndex = updated.lastIndex
                    _queue.value = updated
                }
            }
        }

        _playbackError.value = null
        _currentTrack.value = track
        prepareAndPlay(track)
    }

    fun ensurePlayerInitialized() {
        if (mediaPlayer == null) {
            initializePlayer()
        }
    }

    /**
     * Checks if a PlayerTrack has non-empty metadata and whether its URI/URL is valid and accessible.
     * Prevents IllegalArgumentException, SecurityException, and IOException before passing to MediaPlayer.
     */
    private fun validateTrackAndUri(track: PlayerTrack?): String? {
        if (track == null) {
            return "Track is null"
        }
        if (track.id.isBlank()) {
            return "Track has an empty or invalid ID"
        }
        if (track.title.isBlank()) {
            return "Track metadata missing title"
        }
        
        val streamUrl = track.streamUrl
        if (streamUrl.isBlank()) {
            return "Stream URL/URI is empty"
        }
        
        // Check content URIs
        if (streamUrl.startsWith("content://")) {
            val uri = Uri.parse(streamUrl)
            try {
                // Validate metadata/permissions by attempting to query or open file descriptor
                context.contentResolver.openAssetFileDescriptor(uri, "r")?.use {
                    // Successfully checked permission and presence
                }
            } catch (se: SecurityException) {
                return "Permission denied for content URI: ${se.localizedMessage}"
            } catch (fnf: java.io.FileNotFoundException) {
                return "File not found for content URI: ${fnf.localizedMessage}"
            } catch (e: Exception) {
                return "Failed to open content URI: ${e.localizedMessage}"
            }
        }
        // Check physical file paths
        else if (streamUrl.startsWith("file://") || streamUrl.startsWith("/")) {
            val cleanPath = if (streamUrl.startsWith("file://")) {
                streamUrl.substringAfter("file://")
            } else {
                streamUrl
            }
            val file = java.io.File(cleanPath)
            if (!file.exists()) {
                return "Local file does not exist: $cleanPath"
            }
            if (!file.canRead()) {
                return "Local file is not readable: $cleanPath"
            }
        }
        // Check web URLs syntactically
        else if (streamUrl.startsWith("http://") || streamUrl.startsWith("https://")) {
            try {
                val uri = Uri.parse(streamUrl)
                if (uri.host.isNullOrBlank()) {
                    return "Invalid streaming URL host: $streamUrl"
                }
            } catch (e: Exception) {
                return "Malformed streaming URL: ${e.localizedMessage}"
            }
        } else {
            return "Unsupported URI scheme or format: $streamUrl"
        }
        
        return null
    }

    private fun prepareAndPlay(track: PlayerTrack, positionMs: Long = 0L, playWhenReady: Boolean = true) {
        scope.launch(Dispatchers.IO) {
            val validationError = validateTrackAndUri(track)
            
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                _isBuffering.value = true
                _playbackError.value = null
                
                if (validationError != null) {
                    android.util.Log.e("MusicPlayerManager", "Track validation failed for '${track.title}': $validationError")
                    _playbackError.value = "Cannot play ${track.title}: $validationError"
                    _isBuffering.value = false
                    _isPlaying.value = false
                    _isAudioDeliveringSound.value = false
                    
                    // Advance or pause playback safely to prevent silent freeze
                    val qSize = _queue.value.size
                    if (qSize > 1 && consecutiveValidationFailures < qSize) {
                        consecutiveValidationFailures++
                        android.widget.Toast.makeText(context, "Skipping unplayable track: ${track.title}", android.widget.Toast.LENGTH_SHORT).show()
                        skipNext()
                    } else {
                        consecutiveValidationFailures = 0
                        android.widget.Toast.makeText(context, "Playback stopped: No playable tracks found in playlist.", android.widget.Toast.LENGTH_LONG).show()
                    }
                    return@withContext
                }

                ensurePlayerInitialized()
                
                try {
                    val mp = mediaPlayer ?: return@withContext
                    mp.reset()
                    
                    var streamUrl = track.streamUrl
                    if (track.source == "Locker" && googleAccessToken.isNullOrEmpty() && !googleApiKey.isNullOrEmpty()) {
                        streamUrl = if (streamUrl.contains("?")) {
                            "$streamUrl&key=$googleApiKey"
                        } else {
                            "$streamUrl?key=$googleApiKey"
                        }
                    }
                    
                    val headers = HashMap<String, String>()
                    headers["User-Agent"] = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"
                    if (track.source == "Locker" && !googleAccessToken.isNullOrEmpty()) {
                        headers["Authorization"] = "Bearer $googleAccessToken"
                    }
                    
                    if (streamUrl.startsWith("http")) {
                        mp.setDataSource(context, Uri.parse(streamUrl), headers)
                    } else {
                        mp.setDataSource(context, Uri.parse(streamUrl))
                    }
                    
                    mp.prepareAsync()
                
                mp.setOnPreparedListener { finishedMp ->
                    consecutiveValidationFailures = 0
                    _isBuffering.value = false
                    _duration.value = finishedMp.duration.toLong()
                    if (positionMs > 0L) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            finishedMp.seekTo(positionMs, android.media.MediaPlayer.SEEK_CLOSEST)
                        } else {
                            @Suppress("DEPRECATION")
                            finishedMp.seekTo(positionMs.toInt())
                        }
                    }
                    if (playWhenReady) {
                        if (requestAudioFocus()) {
                            isDuckedDueToVideo = false
                            finishedMp.setVolume(0.0f, 0.0f)
                            finishedMp.start()
                            _isPlaying.value = true
                            _isAudioDeliveringSound.value = true
                            
                            scope.launch {
                                for (i in 1..20) {
                                    kotlinx.coroutines.delay(25)
                                    val vol = i / 20f
                                    if (mediaPlayer == finishedMp && _isPlaying.value && !isDuckedDueToVideo) {
                                        try {
                                            finishedMp.setVolume(vol, vol)
                                        } catch (e: Exception) {}
                                    } else {
                                        break
                                    }
                                }
                            }
                            
                            startProgressTracker()
                            acquireLocks()
                        } else {
                            _isPlaying.value = false
                            _isAudioDeliveringSound.value = false
                        }
                    } else {
                        _isPlaying.value = false
                        _isAudioDeliveringSound.value = false
                    }
                    maybeInitEqualizer()
                    try {
                        val intent = android.content.Intent(context, MyuLocPlaybackService::class.java)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }
                    } catch (e: Throwable) { e.printStackTrace() }
                }
                
                // Persist track references in a single batched operation using our managed scope
                scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                        db.preferenceDao().setPreferences(listOf(
                            com.example.data.database.PreferenceEntity("last_track_id", track.id),
                            com.example.data.database.PreferenceEntity("last_track_title", track.title),
                            com.example.data.database.PreferenceEntity("last_track_artist", track.artist),
                            com.example.data.database.PreferenceEntity("last_track_streamUrl", track.streamUrl),
                            com.example.data.database.PreferenceEntity("last_track_thumbnailUrl", track.thumbnailUrl),
                            com.example.data.database.PreferenceEntity("last_track_source", track.source),
                            com.example.data.database.PreferenceEntity("last_track_durationMs", track.durationMs.toString()),
                            com.example.data.database.PreferenceEntity("last_track_positionMs", positionMs.toString())
                        ))
                    } catch (e: Exception) { e.printStackTrace() }
                }
                
            } catch (e: Exception) {
                _playbackError.value = "Preparation failed: ${e.message}"
                _isBuffering.value = false
                _isPlaying.value = false
                _isAudioDeliveringSound.value = false
            }
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val audioAttributes = android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
                
                audioFocusRequest = android.media.AudioFocusRequest.Builder(android.media.AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .build()
                
                val result = audioManager.requestAudioFocus(audioFocusRequest!!)
                result == android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } else {
                @Suppress("DEPRECATION")
                val result = audioManager.requestAudioFocus(
                    audioFocusChangeListener,
                    android.media.AudioManager.STREAM_MUSIC,
                    android.media.AudioManager.AUDIOFOCUS_GAIN
                )
                result == android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun abandonAudioFocus() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }
    }

    private fun acquireLocks() {
        try {
            if (wakeLock?.isHeld == false) wakeLock?.acquire()
        } catch (e: Exception) { e.printStackTrace() }
        try {
            if (wifiLock?.isHeld == false) wifiLock?.acquire()
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun releaseLocks() {
        try {
            if (wakeLock?.isHeld == true) wakeLock?.release()
        } catch (e: Exception) { e.printStackTrace() }
        try {
            if (wifiLock?.isHeld == true) wifiLock?.release()
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun pausePlayback(releaseFocus: Boolean) {
        runOnMainThread {
            try {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        mp.pause()
                        _isPlaying.value = false
                        _isAudioDeliveringSound.value = false
                        stopProgressTracker()
                    }
                }
                releaseLocks()
                if (releaseFocus) {
                    abandonAudioFocus()
                    playOnFocusGain = false
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun updateServiceState() {
        try {
            val intent = android.content.Intent(context, MyuLocPlaybackService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (e: Throwable) { e.printStackTrace() }
    }

    fun togglePlayPause() {
        runOnMainThread {
            try {
                if (_isBuffering.value) {
                    return@runOnMainThread
                }
                ensurePlayerInitialized()
                val mp = mediaPlayer ?: return@runOnMainThread
                if (mp.isPlaying) {
                    pausePlayback(releaseFocus = true)
                } else {
                    val track = _currentTrack.value
                    if (track != null) {
                        try {
                            if (requestAudioFocus()) {
                                isDuckedDueToVideo = false
                                mp.setVolume(1.0f, 1.0f)
                                mp.start()
                                _isPlaying.value = true
                                _isAudioDeliveringSound.value = true
                                startProgressTracker()
                                acquireLocks()
                            }
                        } catch (e: Exception) {
                            prepareAndPlay(track, _currentPosition.value, playWhenReady = true)
                        }
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun seekTo(positionMs: Long) {
        runOnMainThread {
            try {
                if (_isBuffering.value) {
                    return@runOnMainThread
                }
                mediaPlayer?.let { mp ->
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        mp.seekTo(positionMs, android.media.MediaPlayer.SEEK_CLOSEST)
                    } else {
                        @Suppress("DEPRECATION")
                        mp.seekTo(positionMs.toInt())
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
            _currentPosition.value = positionMs
            savePositionToDb(positionMs)
            _manualSeekEvents.tryEmit(positionMs)
        }
    }

    private fun savePositionToDb(pos: Long) {
        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                db.preferenceDao().setPreference(
                    com.example.data.database.PreferenceEntity("last_track_positionMs", pos.toString())
                )
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun randomizeQueue() {
        runOnMainThread {
            val q = _queue.value
            if (q.isEmpty()) return@runOnMainThread
            
            val currentTrackId = _currentTrack.value?.id
            val randomizedQ = q.shuffled()
            
            if (currentTrackId != null) {
                val newIndex = randomizedQ.indexOfFirst { it.id == currentTrackId }
                if (newIndex != -1) {
                    currentIndex = newIndex
                } else {
                    currentIndex = 0
                }
            } else {
                currentIndex = 0
            }
            
            _queue.value = randomizedQ
        }
    }

    fun sortQueueByLanguage() {
        runOnMainThread {
            val q = _queue.value
            if (q.isEmpty()) return@runOnMainThread
            
            val currentTrackId = _currentTrack.value?.id
            
            // Heuristic: Sort by whether the title contains Asian/CJK characters
            val sortedQ = q.sortedBy { track ->
                val hasAsianChars = track.title.any { it.code in 0x4E00..0x9FFF || it.code in 0x3040..0x309F || it.code in 0x30A0..0x30FF }
                if (hasAsianChars) 0 else 1 
            }
            
            if (currentTrackId != null) {
                val newIndex = sortedQ.indexOfFirst { it.id == currentTrackId }
                if (newIndex != -1) {
                    currentIndex = newIndex
                } else {
                    currentIndex = 0
                }
            } else {
                currentIndex = 0
            }
            
            _queue.value = sortedQ
        }
    }

    fun sortQueueByTitle(ascending: Boolean = true) {
        runOnMainThread {
            val q = _queue.value
            if (q.isEmpty()) return@runOnMainThread
            
            val currentTrackId = _currentTrack.value?.id
            val sortedQ = if (ascending) {
                q.sortedBy { it.title.lowercase() }
            } else {
                q.sortedByDescending { it.title.lowercase() }
            }
            
            if (currentTrackId != null) {
                val newIndex = sortedQ.indexOfFirst { it.id == currentTrackId }
                if (newIndex != -1) {
                    currentIndex = newIndex
                } else {
                    currentIndex = 0
                }
            } else {
                currentIndex = 0
            }
            
            _queue.value = sortedQ
        }
    }

    fun reShuffleRemainingQueue() {
        runOnMainThread {
            val q = _queue.value.toMutableList()
            if (q.size <= currentIndex + 1) return@runOnMainThread
            
            val played = q.subList(0, currentIndex + 1)
            val toShuffle = q.subList(currentIndex + 1, q.size).shuffled()
            val finalQueue = played + toShuffle
            _queue.value = finalQueue
        }
    }

    fun skipNext() {
        runOnMainThread {
            val q = _queue.value
            if (q.isEmpty()) return@runOnMainThread
            if (currentIndex < 0 || currentIndex >= q.size) {
                currentIndex = 0
            }
            
            val currentMode = shuffleMode.value
            if (currentMode == MyuLocShuffleMode.DYNAMIC) {
                songsPlayedInBlock++
                if (songsPlayedInBlock > shuffleInterval.value) {
                    reShuffleRemainingQueue()
                    songsPlayedInBlock = 1
                }
            }
            
            val nextIdx = currentIndex + 1
            if (nextIdx >= q.size) {
                if (repeatMode.value == PlaybackRepeatMode.ALL) {
                    currentIndex = 0
                } else {
                    mediaPlayer?.stop()
                    _isPlaying.value = false
                    _isAudioDeliveringSound.value = false
                    return@runOnMainThread
                }
            } else {
                currentIndex = nextIdx
            }
            
            val track = q[currentIndex]
            _currentTrack.value = track
            prepareAndPlay(track)
        }
    }

    fun skipPrevious() {
        runOnMainThread {
            val q = _queue.value
            if (q.isEmpty()) return@runOnMainThread
            if (currentIndex < 0 || currentIndex >= q.size) {
                currentIndex = 0
            }
            
            val currentMode = shuffleMode.value
            if (currentMode == MyuLocShuffleMode.DYNAMIC) {
                songsPlayedInBlock = (songsPlayedInBlock - 1).coerceAtLeast(1)
            }
            
            var prevIdx = currentIndex - 1
            if (prevIdx < 0) {
                if (repeatMode.value == PlaybackRepeatMode.ALL) {
                    prevIdx = q.lastIndex
                } else {
                    prevIdx = 0
                }
            }
            currentIndex = prevIdx
            
            val track = q[currentIndex]
            _currentTrack.value = track
            prepareAndPlay(track)
        }
    }

    private fun handleTrackEnded() {
        runOnMainThread {
            _isAudioDeliveringSound.value = false
            _currentPosition.value = 0L
            
            val mode = repeatMode.value
            if (mode == PlaybackRepeatMode.ONE) {
                val track = _currentTrack.value
                if (track != null) {
                    prepareAndPlay(track)
                } else {
                    skipNext()
                }
            } else {
                skipNext()
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        var lastSavedTime = 0L
        progressJob = scope.launch {
            while (true) {
                mediaPlayer?.let { mp ->
                    try {
                        if (mp.isPlaying) {
                            val currentPos = mp.currentPosition.toLong()
                            _currentPosition.value = currentPos
                            
                            val now = System.currentTimeMillis()
                            if (now - lastSavedTime >= 5000L) {
                                lastSavedTime = now
                                savePositionToDb(currentPos)
                            }
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                }
                delay(250)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        try {
            scope.coroutineContext[kotlinx.coroutines.Job]?.cancel()
        } catch (e: Exception) { }
        stopProgressTracker()
        abandonAudioFocus()
        try {
            noisyReceiver?.let { context.unregisterReceiver(it) }
        } catch (e: Exception) { }
        noisyReceiver = null
        
        try {
            if (wifiLock?.isHeld == true) wifiLock?.release()
            wifiLock = null
        } catch (e: Exception) { }
        
        try {
            if (wakeLock?.isHeld == true) wakeLock?.release()
            wakeLock = null
        } catch (e: Exception) { }

        try {
            val intent = android.content.Intent(context, MyuLocPlaybackService::class.java)
            context.stopService(intent)
        } catch (e: Exception) { }

        try {
            synchronized(equalizerLock) {
                equalizer?.release()
                equalizer = null
            }
        } catch (e: Exception) { }

        try {
            mediaPlayer?.release()
        } catch (e: Exception) { }
        mediaPlayer = null
        
        INSTANCE = null
    }
}

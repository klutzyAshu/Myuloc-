package com.example.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
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

    var exoPlayer: ExoPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null
    private var playJob: Job? = null
    private var wifiLock: android.net.wifi.WifiManager.WifiLock? = null
    private var wakeLock: android.os.PowerManager.WakeLock? = null

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

    private val _playbackError = MutableStateFlow<String?>(null)
    val playbackError: StateFlow<String?> = _playbackError

    // Queue management
    private val _queue = MutableStateFlow<List<PlayerTrack>>(emptyList())
    val queue: StateFlow<List<PlayerTrack>> = _queue

    private var currentIndex = -1
    private var googleAccessToken: String? = null
    private var googleApiKey: String? = null
    private var previewEndedWarned = false

    // Equalizer variables
    private var equalizer: android.media.audiofx.Equalizer? = null
    val eqBands = MutableStateFlow(listOf(0, 0, 0, 0, 0)) // MilliBels (e.g. -1500 to 1500 limit is standard for +/-15dB)
    val eqEnabled = MutableStateFlow(false)

    // Shuffle and Repeat states
    val shuffleEnabled = MutableStateFlow(false)
    val repeatMode = MutableStateFlow(PlaybackRepeatMode.OFF)

    init {
        initializePlayer()
        
        // Restore saved state (shuffle, repeat, last track and last position) in background IO thread
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                val savedShuffle = db.preferenceDao().getPreference("shuffle_enabled")?.toBoolean() ?: false
                shuffleEnabled.value = savedShuffle
                
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
                    val savedIndex = db.preferenceDao().getPreference("last_track_index")?.toIntOrNull() ?: 0
                    
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
                        prepareAndPlay(restoredTrack, savedPos, playWhenReady = false)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Persistence observer for shuffle state
            launch {
                shuffleEnabled.collect { value ->
                    try {
                        val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                        db.preferenceDao().setPreference(
                            com.example.data.database.PreferenceEntity("shuffle_enabled", value.toString())
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // Persistence observer for repeat mode state
            launch {
                repeatMode.collect { value ->
                    try {
                        val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                        db.preferenceDao().setPreference(
                            com.example.data.database.PreferenceEntity("repeat_mode", value.name)
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // Persistence observer for current track properties and track index
            launch {
                _currentTrack.collect { track ->
                    if (track != null) {
                        try {
                            val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                            db.preferenceDao().setPreference(com.example.data.database.PreferenceEntity("last_track_id", track.id))
                            db.preferenceDao().setPreference(com.example.data.database.PreferenceEntity("last_track_title", track.title))
                            db.preferenceDao().setPreference(com.example.data.database.PreferenceEntity("last_track_artist", track.artist))
                            db.preferenceDao().setPreference(com.example.data.database.PreferenceEntity("last_track_streamUrl", track.streamUrl))
                            db.preferenceDao().setPreference(com.example.data.database.PreferenceEntity("last_track_thumbnailUrl", track.thumbnailUrl))
                            db.preferenceDao().setPreference(com.example.data.database.PreferenceEntity("last_track_source", track.source))
                            db.preferenceDao().setPreference(com.example.data.database.PreferenceEntity("last_track_durationMs", track.durationMs.toString()))
                            db.preferenceDao().setPreference(com.example.data.database.PreferenceEntity("last_track_index", currentIndex.toString()))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            // Persistence observer for entire play queue list on updates
            launch {
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

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        runOnMainThread {
            if (exoPlayer != null) return@runOnMainThread
            try {
                // Initialize WifiLock to keep network connected under background/Doze state
                try {
                    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
                    wifiLock = wifiManager.createWifiLock(android.net.wifi.WifiManager.WIFI_MODE_FULL_HIGH_PERF, "MyuLoc:WifiLock")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    val powerManager = context.applicationContext.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
                    wakeLock = powerManager.newWakeLock(android.os.PowerManager.PARTIAL_WAKE_LOCK, "MyuLoc:WakeLock")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val loadControl = androidx.media3.exoplayer.DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        5000,  // minBufferMs: reduce load threshold to 5 seconds to load incredibly fast
                        15000, // maxBufferMs: max of 15 seconds loaded at once
                        500,   // bufferForPlaybackMs: play immediately after only 500ms buffered (smooth, fast transition!)
                        1000   // bufferForPlaybackAfterRebufferMs: resume play after only 1 second of rebuffering
                    )
                    .build()

                val audioAttributes = androidx.media3.common.AudioAttributes.Builder()
                    .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(androidx.media3.common.C.USAGE_MEDIA)
                    .build()

                val renderersFactory = androidx.media3.exoplayer.DefaultRenderersFactory(context).apply {
                    setExtensionRendererMode(androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                    setEnableDecoderFallback(true)
                }

                val extractorsFactory = androidx.media3.extractor.DefaultExtractorsFactory().apply {
                    setConstantBitrateSeekingEnabled(true)
                    setMp3ExtractorFlags(androidx.media3.extractor.mp3.Mp3Extractor.FLAG_ENABLE_INDEX_SEEKING)
                }

                val mediaSourceFactory = androidx.media3.exoplayer.source.DefaultMediaSourceFactory(context, extractorsFactory)

                exoPlayer = ExoPlayer.Builder(context, renderersFactory)
                    .setMediaSourceFactory(mediaSourceFactory)
                    .setLooper(android.os.Looper.getMainLooper())
                    .setLoadControl(loadControl)
                    .setAudioAttributes(audioAttributes, true)
                    .build().apply {
                    setWakeMode(androidx.media3.common.C.WAKE_MODE_LOCAL) // Hold CPU wake lock during active playback
                    setRepeatMode(Player.REPEAT_MODE_OFF)
                    addListener(object : Player.Listener {
                        override fun onIsPlayingChanged(isPlayingChanged: Boolean) {
                            _isPlaying.value = isPlayingChanged
                            if (isPlayingChanged) {
                                try {
                                    if (wifiLock?.isHeld == false) {
                                        wifiLock?.acquire()
                                        android.util.Log.d("MusicPlayerManager", "Background WifiLock successfully acquired.")
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                try {
                                    if (wakeLock?.isHeld == false) {
                                        wakeLock?.acquire(12 * 60 * 60 * 1000L /* 12 hours timeout safety limit */)
                                        android.util.Log.d("MusicPlayerManager", "Background WakeLock successfully acquired.")
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                startProgressTracker()
                             } else {
                                try {
                                    if (wifiLock?.isHeld == true) {
                                        wifiLock?.release()
                                        android.util.Log.d("MusicPlayerManager", "Background WifiLock safely released.")
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                try {
                                    if (wakeLock?.isHeld == true) {
                                        wakeLock?.release()
                                        android.util.Log.d("MusicPlayerManager", "Background WakeLock safely released.")
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                stopProgressTracker()
                            }
                        }

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            when (playbackState) {
                                Player.STATE_BUFFERING -> {
                                    _isBuffering.value = true
                                }
                                Player.STATE_READY -> {
                                    _isBuffering.value = false
                                    _duration.value = this@apply.duration
                                    _playbackError.value = null
                                    // Attempt to initialize equalizer if not already done
                                    maybeInitEqualizer()
                                }
                                Player.STATE_ENDED -> {
                                    _isBuffering.value = false
                                    handleTrackEnded()
                                }
                                Player.STATE_IDLE -> {
                                    _isBuffering.value = false
                                }
                            }
                        }

                        override fun onPlayerError(error: PlaybackException) {
                            _isBuffering.value = false
                            android.util.Log.e("MusicPlayerManager", "onPlayerError parsed: errorCode=${error.errorCode}, localizedMessage=${error.localizedMessage}")
                            
                            // Check if decoding failed during rapid format switching or live streams
                            if (error.errorCode == PlaybackException.ERROR_CODE_DECODING_FAILED ||
                                error.errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED ||
                                error.errorCode == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED) {
                                
                                android.util.Log.w("MusicPlayerManager", "Format-switching decoding issue detected. Clear pipeline and re-init decoder safely.")
                                runOnMainThread {
                                    try {
                                        exoPlayer?.let { player ->
                                            val currentPos = player.currentPosition
                                            val currentItem = player.currentMediaItem
                                            if (currentItem != null) {
                                                player.stop()
                                                player.clearMediaItems()
                                                player.setMediaItem(currentItem, currentPos)
                                                player.prepare()
                                                player.playWhenReady = true
                                                android.util.Log.i("MusicPlayerManager", "Decoding pipeline re-prepared and recovered.")
                                            }
                                        }
                                    } catch (e: Exception) {
                                        _playbackError.value = "Playback Error (Fallback): ${error.localizedMessage}"
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                                _playbackError.value = "Playback Error: ${error.localizedMessage}"
                            }
                            error.printStackTrace()
                        }

                        override fun onMediaMetadataChanged(mediaMetadata: androidx.media3.common.MediaMetadata) {
                            super.onMediaMetadataChanged(mediaMetadata)
                            android.util.Log.d("MusicPlayerManager", "Wiretapped media metadata: title=${mediaMetadata.title}, artist=${mediaMetadata.artist}")
                            
                            val current = _currentTrack.value
                            if (current != null && (!mediaMetadata.title.isNullOrEmpty() || !mediaMetadata.artist.isNullOrEmpty())) {
                                val updatedTrack = current.copy(
                                    title = if (!mediaMetadata.title.isNullOrEmpty()) mediaMetadata.title.toString() else current.title,
                                    artist = if (!mediaMetadata.artist.isNullOrEmpty()) mediaMetadata.artist.toString() else current.artist
                                )
                                if (updatedTrack != current) {
                                    _currentTrack.value = updatedTrack
                                    android.util.Log.d("MusicPlayerManager", "Dynamically enriched track information from wiretapped metadata.")
                                }
                            }
                        }

                        override fun onMetadata(metadata: androidx.media3.common.Metadata) {
                            super.onMetadata(metadata)
                            android.util.Log.d("MusicPlayerManager", "Wiretapped raw codec metadata block intercepted.")
                        }

                        override fun onAudioSessionIdChanged(audioSessionId: Int) {
                            reinitEqualizer(audioSessionId)
                        }
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Register audio becoming noisy broadcast receiver to pause playback when headset unplugged
            val noisyReceiver = object : android.content.BroadcastReceiver() {
                override fun onReceive(context: android.content.Context, intent: android.content.Intent) {
                    if (android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                        val pendingResult = goAsync()
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                            var shouldPause = true
                            try {
                                val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                                val pref = db.preferenceDao().getPreference("headset_disconnect_behavior")
                                if (pref != null) {
                                    shouldPause = pref.toBoolean()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (shouldPause) {
                                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                    if (isPlaying.value) {
                                        togglePlayPause()
                                    }
                                }
                            }
                            pendingResult.finish()
                        }
                    }
                }
            }
            val intentFilter = android.content.IntentFilter(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    context.registerReceiver(
                        noisyReceiver,
                        intentFilter,
                        android.content.Context.RECEIVER_NOT_EXPORTED
                    )
                } else {
                    context.registerReceiver(noisyReceiver, intentFilter)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun maybeInitEqualizer() {
        val player = exoPlayer ?: return
        if (equalizer == null && player.audioSessionId != android.media.AudioManager.AUDIO_SESSION_ID_GENERATE) {
            reinitEqualizer(player.audioSessionId)
        }
    }

    private fun isEmulator(): Boolean {
        val brand = android.os.Build.BRAND
        val device = android.os.Build.DEVICE
        val model = android.os.Build.MODEL
        val hardware = android.os.Build.HARDWARE
        val product = android.os.Build.PRODUCT
        val fingerprint = android.os.Build.FINGERPRINT
        return fingerprint.contains("generic") ||
                fingerprint.contains("unknown") ||
                model.contains("google_sdk") ||
                model.contains("Emulator") ||
                model.contains("Android SDK built for x86") ||
                hardware.contains("goldfish") ||
                hardware.contains("ranchu") ||
                (brand.startsWith("generic") && device.startsWith("generic")) ||
                "google_sdk" == product
    }

    private fun reinitEqualizer(sessionId: Int) {
        try {
            equalizer?.release()
            equalizer = null
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        if (isEmulator()) {
            android.util.Log.d("MusicPlayerManager", "Running in simulator. Skipping physical equalizer instance to avoid native crash.")
            return
        }

        var eqAvailable = false
        try {
            val descriptors = android.media.audiofx.AudioEffect.queryEffects()
            for (desc in descriptors) {
                if (desc.type == android.media.audiofx.AudioEffect.EFFECT_TYPE_EQUALIZER) {
                    eqAvailable = true
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!eqAvailable) {
            android.util.Log.d("MusicPlayerManager", "Equalizer effect is not supported by device hardware. Skipping.")
            return
        }

        try {
            if (sessionId != android.media.AudioManager.AUDIO_SESSION_ID_GENERATE) {
                equalizer = android.media.audiofx.Equalizer(0, sessionId).apply {
                    enabled = eqEnabled.value
                    // Apply current band levels
                    eqBands.value.forEachIndexed { bandIndex, level ->
                        if (bandIndex < numberOfBands) {
                            setBandLevel(bandIndex.toShort(), level.toShort())
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun setEqEnabled(enabled: Boolean) {
        eqEnabled.value = enabled
        try {
            equalizer?.enabled = enabled
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun setEqBandLevel(band: Int, levelMilliBels: Int) {
        val currentLevels = eqBands.value.toMutableList()
        if (band in currentLevels.indices) {
            currentLevels[band] = levelMilliBels
            eqBands.value = currentLevels
            try {
                equalizer?.let { eq ->
                    if (band < eq.numberOfBands) {
                        eq.setBandLevel(band.toShort(), levelMilliBels.toShort())
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
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
            exoPlayer?.stop()
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun setGoogleAccessToken(token: String?) {
        googleAccessToken = token
    }

    fun setGoogleApiKey(key: String?) {
        googleApiKey = key
    }

    fun playTrack(track: PlayerTrack, newQueue: List<PlayerTrack> = emptyList()) {
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

        _playbackError.value = null
        _currentTrack.value = track
        prepareAndPlay(track)
    }

    fun ensurePlayerInitialized() {
        if (exoPlayer == null) {
            android.util.Log.i("MusicPlayerManager", "ensurePlayerInitialized - Re-initializing ExoPlayer.")
            initializePlayer()
        }
    }

    @OptIn(UnstableApi::class)
    private fun prepareAndPlay(track: PlayerTrack, positionMs: Long = 0L, playWhenReady: Boolean = true) {
        previewEndedWarned = false
        runOnMainThread {
            // Cancel active media preparation instantly to prevent async race conditions
            playJob?.cancel()
            _isBuffering.value = true
            _playbackError.value = null
            
            try {
                exoPlayer?.let { player ->
                    player.stop()
                    player.clearMediaItems()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            playJob = scope.launch(Dispatchers.Main) {
                ensurePlayerInitialized()
                val player = exoPlayer ?: return@launch
                
                val currentIdxSaved = currentIndex
                launch(Dispatchers.IO) {
                    try {
                        val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                        db.preferenceDao().setPreference(
                            com.example.data.database.PreferenceEntity("last_track_index", currentIdxSaved.toString())
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Offload MediaSource and MediaItem generation entirely to Dispatchers.IO executor
                val mediaSource = kotlinx.coroutines.withContext(Dispatchers.IO) {
                    var streamUrl = track.streamUrl
                    if (track.source == "Locker" && googleAccessToken.isNullOrEmpty() && !googleApiKey.isNullOrEmpty()) {
                        streamUrl = if (streamUrl.contains("?")) {
                            "$streamUrl&key=$googleApiKey"
                        } else {
                            "$streamUrl?key=$googleApiKey"
                        }
                    }
                    val uri = Uri.parse(streamUrl)
                    
                    // Build rich MediaMetadata for notifications, bluetooth metadata, lockscreens and Dynamic Islands
                    val mediaMetadata = androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artist)
                        .setArtworkUri(if (track.thumbnailUrl.isNotEmpty()) Uri.parse(track.thumbnailUrl) else null)
                        .build()
                        
                    val mediaItem = MediaItem.Builder()
                        .setUri(uri)
                        .setMediaMetadata(mediaMetadata)
                        .build()
                    
                    // Yield thread cooperatively to check for cancel request before doing heavy preparation
                    kotlinx.coroutines.yield()

                    // Use DefaultDataSource.Factory to support both HTTP streams and local file/MediaStore content URIs!
                    if (track.source == "Locker" && !googleAccessToken.isNullOrEmpty()) {
                        val token = googleAccessToken
                        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
                            .setAllowCrossProtocolRedirects(true)
                            .setDefaultRequestProperties(mapOf("Authorization" to "Bearer $token"))
                        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
                        ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(mediaItem)
                    } else {
                        // General progressive source (for Search stream or Demo streams)
                        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
                            .setAllowCrossProtocolRedirects(true)
                        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
                        ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(mediaItem)
                    }
                }

                // Check cancellation state once more before passing to ExoPlayer on main thread
                kotlinx.coroutines.yield()

                try {
                    player.setMediaSource(mediaSource)
                    if (positionMs > 0L) {
                        player.seekTo(positionMs)
                    }
                    player.prepare()
                    player.playWhenReady = playWhenReady
                    _isPlaying.value = playWhenReady
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Launch Media3 Playback Service to persist playback and register MediaSession.
                try {
                    val intent = android.content.Intent(context, MyuLocPlaybackService::class.java)
                    context.startService(intent)
                } catch (e: Throwable) {
                    android.util.Log.e("MusicPlayerManager", "Failed to start playback service securely: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }

    fun togglePlayPause() {
        runOnMainThread {
            try {
                ensurePlayerInitialized()
                val player = exoPlayer ?: return@runOnMainThread
                if (player.playbackState == Player.STATE_IDLE || player.mediaItemCount == 0) {
                    val track = _currentTrack.value
                    if (track != null) {
                        prepareAndPlay(track, _currentPosition.value, playWhenReady = true)
                        return@runOnMainThread
                    }
                }
                if (player.isPlaying) {
                    player.pause()
                } else {
                    if (player.playbackState == Player.STATE_ENDED) {
                        player.seekTo(0)
                        player.prepare()
                    }
                    player.play()
                }
            } catch (e: Exception) {
                android.util.Log.e("MusicPlayerManager", "Exception in togglePlayPause: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        runOnMainThread {
            try {
                val player = exoPlayer
                if (player != null && (player.playbackState != Player.STATE_IDLE && player.mediaItemCount > 0)) {
                    player.seekTo(positionMs)
                }
            } catch (e: Exception) {
                android.util.Log.e("MusicPlayerManager", "Exception in seekTo: ${e.message}")
                e.printStackTrace()
            }
            _currentPosition.value = positionMs
            
            val track = _currentTrack.value
            if (track != null) {
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    try {
                        val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                        db.preferenceDao().setPreference(
                            com.example.data.database.PreferenceEntity("last_track_positionMs", positionMs.toString())
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun skipNext() {
        runOnMainThread {
            val q = _queue.value
            if (q.isEmpty()) return@runOnMainThread
            if (currentIndex < 0 || currentIndex >= q.size) {
                currentIndex = 0
            }
            
            if (shuffleEnabled.value) {
                val nextIdx = if (q.size > 1) {
                    var r = (0 until q.size).random()
                    while (r == currentIndex) {
                        r = (0 until q.size).random()
                    }
                    r
                } else {
                    0
                }
                currentIndex = nextIdx
            } else {
                val nextIdx = currentIndex + 1
                if (nextIdx >= q.size) {
                    if (repeatMode.value == PlaybackRepeatMode.ALL) {
                        currentIndex = 0
                    } else {
                        // End of list, pause or stop
                        exoPlayer?.stop()
                        _isPlaying.value = false
                        return@runOnMainThread
                    }
                } else {
                    currentIndex = nextIdx
                }
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
            
            if (shuffleEnabled.value) {
                val prevIdx = if (q.size > 1) {
                    var r = (0 until q.size).random()
                    while (r == currentIndex) {
                        r = (0 until q.size).random()
                    }
                    r
                } else {
                    0
                }
                currentIndex = prevIdx
            } else {
                var prevIdx = currentIndex - 1
                if (prevIdx < 0) {
                    if (repeatMode.value == PlaybackRepeatMode.ALL) {
                        prevIdx = q.lastIndex
                    } else {
                        prevIdx = 0
                    }
                }
                currentIndex = prevIdx
            }
            
            val track = q[currentIndex]
            _currentTrack.value = track
            prepareAndPlay(track)
        }
    }

    private fun handleTrackEnded() {
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

    private fun startProgressTracker() {
        progressJob?.cancel()
        var lastSavedTime = 0L
        progressJob = scope.launch {
            while (true) {
                exoPlayer?.let { player ->
                    if (player.isPlaying) {
                        _currentPosition.value = player.currentPosition
                        
                        val now = System.currentTimeMillis()
                        if (now - lastSavedTime >= 5000L) {
                            lastSavedTime = now
                            val currentPos = player.currentPosition
                            val track = _currentTrack.value
                            if (track != null && currentPos > 0L) {
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                    try {
                                        val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                                        db.preferenceDao().setPreference(
                                            com.example.data.database.PreferenceEntity("last_track_positionMs", currentPos.toString())
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                    

                }
                delay(300)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
        
        val currentPos = _currentPosition.value
        val track = _currentTrack.value
        if (track != null && currentPos > 0L) {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                try {
                    val db = com.example.data.database.MyuLocDatabase.getDatabase(context)
                    db.preferenceDao().setPreference(
                        com.example.data.database.PreferenceEntity("last_track_positionMs", currentPos.toString())
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private var isReleasing = false

    fun release() {
        synchronized(this) {
            if (isReleasing) return
            isReleasing = true
        }
        stopProgressTracker()
        try {
            if (wifiLock?.isHeld == true) {
                wifiLock?.release()
            }
            wifiLock = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
            wakeLock = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            val intent = android.content.Intent(context, MyuLocPlaybackService::class.java)
            context.stopService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            equalizer?.release()
            equalizer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            exoPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        exoPlayer = null
        synchronized(this) {
            isReleasing = false
            INSTANCE = null
        }
    }
}

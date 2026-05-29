package com.example.player

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.MainActivity

class MyuLocPlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    // Power management locks
    private var serviceWakeLock: android.os.PowerManager.WakeLock? = null
    private var serviceWifiLock: android.net.wifi.WifiManager.WifiLock? = null

    // Synchronized player listener for power management and notification lifecycle
    private val servicePlayerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            android.util.Log.d("MyuLocPlaybackService", "servicePlayerListener.onIsPlayingChanged isPlaying=$isPlaying")
            if (isPlaying) {
                acquireServiceLocks()
            } else {
                releaseServiceLocks()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            android.util.Log.d("MyuLocPlaybackService", "servicePlayerListener.onPlaybackStateChanged State=$playbackState")
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    acquireServiceLocks()
                }
                Player.STATE_IDLE, Player.STATE_ENDED -> {
                    releaseServiceLocks()
                }
            }
        }
    }

    private fun acquireServiceLocks() {
        try {
            if (serviceWifiLock == null) {
                val wifiManager = applicationContext.getSystemService(android.content.Context.WIFI_SERVICE) as android.net.wifi.WifiManager
                serviceWifiLock = wifiManager.createWifiLock(android.net.wifi.WifiManager.WIFI_MODE_FULL_HIGH_PERF, "MyuLoc:ServiceWifiLock")
            }
            if (serviceWifiLock?.isHeld == false) {
                serviceWifiLock?.acquire()
                android.util.Log.i("MyuLocPlaybackService", "WIFILOCK successfully acquired by Service on streaming start.")
            }
        } catch (e: Exception) {
            android.util.Log.e("MyuLocPlaybackService", "Failed to acquire service wifi lock: ${e.message}")
        }

        try {
            if (serviceWakeLock == null) {
                val powerManager = applicationContext.getSystemService(android.content.Context.POWER_SERVICE) as android.os.PowerManager
                serviceWakeLock = powerManager.newWakeLock(android.os.PowerManager.PARTIAL_WAKE_LOCK, "MyuLoc:ServiceWakeLock")
            }
            if (serviceWakeLock?.isHeld == false) {
                serviceWakeLock?.acquire(12 * 60 * 60 * 1000L /* 12h max safety constraint */)
                android.util.Log.i("MyuLocPlaybackService", "WAKELOCK successfully acquired by Service on streaming start.")
            }
        } catch (e: Exception) {
            android.util.Log.e("MyuLocPlaybackService", "Failed to acquire service wake lock: ${e.message}")
        }
    }

    private fun releaseServiceLocks() {
        try {
            if (serviceWifiLock?.isHeld == true) {
                serviceWifiLock?.release()
                android.util.Log.i("MyuLocPlaybackService", "WIFILOCK successfully and safely released by Service.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            if (serviceWakeLock?.isHeld == true) {
                serviceWakeLock?.release()
                android.util.Log.i("MyuLocPlaybackService", "WAKELOCK successfully and safely released by Service.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @UnstableApi
    override fun onCreate() {
        super.onCreate()
        
        // Build custom notification provider using our designated channel ID and strings
        try {
            val defaultProvider = androidx.media3.session.DefaultMediaNotificationProvider.Builder(this)
                .setChannelId("myuloc_playback_channel")
                .setChannelName(com.example.R.string.playback_channel_name)
                .build()

            val provider = object : androidx.media3.session.MediaNotification.Provider {
                override fun createNotification(
                    mediaSession: MediaSession,
                    customLayout: com.google.common.collect.ImmutableList<androidx.media3.session.CommandButton>,
                    actionFactory: androidx.media3.session.MediaNotification.ActionFactory,
                    onNotificationChangedCallback: androidx.media3.session.MediaNotification.Provider.Callback
                ): androidx.media3.session.MediaNotification {
                    val mediaNotification = defaultProvider.createNotification(
                        mediaSession,
                        customLayout,
                        actionFactory,
                        onNotificationChangedCallback
                    )
                    try {
                        val player = mediaSession.player
                        val isPlayActive = player.isPlaying || player.playbackState == Player.STATE_BUFFERING
                        if (isPlayActive) {
                            mediaNotification.notification.flags = mediaNotification.notification.flags or android.app.Notification.FLAG_ONGOING_EVENT or android.app.Notification.FLAG_NO_CLEAR
                        } else {
                            mediaNotification.notification.flags = mediaNotification.notification.flags and (android.app.Notification.FLAG_ONGOING_EVENT.inv()) and (android.app.Notification.FLAG_NO_CLEAR.inv())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return mediaNotification
                }

                override fun handleCustomCommand(
                    session: MediaSession,
                    action: String,
                    extras: android.os.Bundle
                ): Boolean {
                    return defaultProvider.handleCustomCommand(session, action, extras)
                }
            }
            setMediaNotificationProvider(provider)
            android.util.Log.d("MyuLocPlaybackService", "Custom ongoing-aware MediaNotificationProvider delegation successfully applied.")
        } catch (e: Exception) {
            android.util.Log.e("MyuLocPlaybackService", "Error configuring MediaNotificationProvider: ${e.message}")
            e.printStackTrace()
        }
        
        initializeSession()
    }

    @UnstableApi
    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        val player = session.player
        val isPlaybackActive = player.playWhenReady && player.playbackState != Player.STATE_IDLE
        val shouldForceForeground = startInForegroundRequired || isPlaybackActive
        android.util.Log.d("MyuLocPlaybackService", "onUpdateNotification startInForegroundRequired=$startInForegroundRequired, isPlaybackActive=$isPlaybackActive -> shouldForceForeground=$shouldForceForeground")
        super.onUpdateNotification(session, shouldForceForeground)
    }

    @UnstableApi
    private fun initializeSession() {
        if (mediaSession != null) return

        val manager = MusicPlayerManager.getInstance(applicationContext)
        val player = manager.exoPlayer

        if (player != null) {
            // Guarantee synchronized state updates by registering the service-level listener
            player.removeListener(servicePlayerListener)
            player.addListener(servicePlayerListener)

            // Acquire initial lock state if player is already playing when initialization triggers
            if (player.isPlaying) {
                acquireServiceLocks()
            }

            // ForwardingPlayer to intercept next and previous media buttons
            val forwardingPlayer = object : androidx.media3.common.ForwardingPlayer(player) {
                override fun seekToNext() {
                    manager.skipNext()
                }

                override fun seekToNextMediaItem() {
                    manager.skipNext()
                }

                override fun seekToPrevious() {
                    manager.skipPrevious()
                }

                override fun seekToPreviousMediaItem() {
                    manager.skipPrevious()
                }

                override fun getAvailableCommands(): Player.Commands {
                    return super.getAvailableCommands().buildUpon()
                        .add(Player.COMMAND_SEEK_TO_NEXT)
                        .add(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                        .add(Player.COMMAND_SEEK_TO_PREVIOUS)
                        .add(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                        .build()
                }

                override fun isCommandAvailable(command: Int): Boolean {
                    return when (command) {
                        Player.COMMAND_SEEK_TO_NEXT,
                        Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM,
                        Player.COMMAND_SEEK_TO_PREVIOUS,
                        Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM -> true
                        else -> super.isCommandAvailable(command)
                    }
                }
            }

            // Click action on Notification returns back to MainActivity UI
            val activityIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val sessionCallback = object : MediaSession.Callback {
                private var clickCount = 0
                private val clickHandler = android.os.Handler(android.os.Looper.getMainLooper())
                private val clickRunnable = Runnable {
                    val manager = MusicPlayerManager.getInstance(applicationContext)
                    when (clickCount) {
                        1 -> manager.togglePlayPause()
                        2 -> manager.skipNext()
                        3 -> manager.skipPrevious()
                    }
                    clickCount = 0
                }

                override fun onMediaButtonEvent(
                    session: MediaSession,
                    controllerInfo: MediaSession.ControllerInfo,
                    intent: Intent
                ): Boolean {
                    val keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT) as? android.view.KeyEvent
                    if (keyEvent != null && keyEvent.action == android.view.KeyEvent.ACTION_UP) {
                        val keyCode = keyEvent.keyCode
                        if (keyCode == android.view.KeyEvent.KEYCODE_HEADSETHOOK || keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                            clickCount++
                            clickHandler.removeCallbacks(clickRunnable)
                            // Wait 350ms to accumulate double/triple clicks
                            clickHandler.postDelayed(clickRunnable, 350)
                            return true
                        }
                    }
                    return super.onMediaButtonEvent(session, controllerInfo, intent)
                }
            }

            try {
                mediaSession = MediaSession.Builder(this, forwardingPlayer)
                    .setSessionActivity(pendingIntent)
                    .setCallback(sessionCallback)
                    .build()
                android.util.Log.d("MyuLocPlaybackService", "MediaSession successfully built and bound.")
            } catch (e: Exception) {
                android.util.Log.e("MyuLocPlaybackService", "Error building MediaSession: ${e.message}")
                e.printStackTrace()
            }
        } else {
            android.util.Log.w("MyuLocPlaybackService", "ExoPlayer was null during session initialization!")
        }
    }

    private fun releaseSession() {
        try {
            val manager = MusicPlayerManager.getInstance(applicationContext)
            manager.exoPlayer?.removeListener(servicePlayerListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        mediaSession?.run {
            release()
            mediaSession = null
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        val manager = MusicPlayerManager.getInstance(applicationContext)
        manager.ensurePlayerInitialized()

        val currentPlaySession = mediaSession
        val expectedPlayer = manager.exoPlayer
        val boundPlayer = (currentPlaySession?.player as? androidx.media3.common.ForwardingPlayer)?.wrappedPlayer

        if (currentPlaySession == null || expectedPlayer != boundPlayer) {
            android.util.Log.i("MyuLocPlaybackService", "Rebuilding MediaSession due to player/session mismatch.")
            releaseSession()
            initializeSession()
        }
        return mediaSession
    }

    @UnstableApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val manager = MusicPlayerManager.getInstance(applicationContext)
        manager.ensurePlayerInitialized()

        val currentPlaySession = mediaSession
        val expectedPlayer = manager.exoPlayer
        val boundPlayer = (currentPlaySession?.player as? androidx.media3.common.ForwardingPlayer)?.wrappedPlayer

        if (currentPlaySession == null || expectedPlayer != boundPlayer) {
            android.util.Log.i("MyuLocPlaybackService", "onStartCommand - Rebuilding MediaSession.")
            releaseSession()
            initializeSession()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        releaseServiceLocks()
        releaseSession()
        try {
            MusicPlayerManager.instance?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}

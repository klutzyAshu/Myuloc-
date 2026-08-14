package com.example.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Build
import android.os.IBinder
import kotlinx.coroutines.*

class MyuLocPlaybackService : Service() {

    private val CHANNEL_ID = "MyuLoc_Playback"
    private val NOTIFICATION_ID = 1001

    private lateinit var mediaSession: MediaSession
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        mediaSession = MediaSession(this, "MyuLocMediaSession").apply {
            setCallback(object : MediaSession.Callback() {
                private var clickCount = 0
                private var clickJob: Job? = null

                override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                    val keyEvent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, android.view.KeyEvent::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        mediaButtonIntent.getParcelableExtra<android.view.KeyEvent>(Intent.EXTRA_KEY_EVENT)
                    }
                    if (keyEvent != null && keyEvent.action == android.view.KeyEvent.ACTION_DOWN) {
                        val keyCode = keyEvent.keyCode
                        if (keyCode == android.view.KeyEvent.KEYCODE_MEDIA_NEXT) {
                            MusicPlayerManager.getInstance(this@MyuLocPlaybackService).skipNext()
                            return true
                        }
                        if (keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                            MusicPlayerManager.getInstance(this@MyuLocPlaybackService).skipPrevious()
                            return true
                        }
                        if (keyCode == android.view.KeyEvent.KEYCODE_HEADSETHOOK || 
                            keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
                            keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PLAY ||
                            keyCode == android.view.KeyEvent.KEYCODE_MEDIA_PAUSE) {
                            
                            clickCount++
                            clickJob?.cancel()
                            clickJob = scope.launch(Dispatchers.Main) {
                                delay(300) // slightly faster response (300ms)
                                when (clickCount) {
                                    1 -> {
                                        MusicPlayerManager.getInstance(this@MyuLocPlaybackService).togglePlayPause()
                                    }
                                    2 -> {
                                        MusicPlayerManager.getInstance(this@MyuLocPlaybackService).skipNext()
                                    }
                                    3 -> {
                                        MusicPlayerManager.getInstance(this@MyuLocPlaybackService).skipPrevious()
                                    }
                                    else -> {
                                        MusicPlayerManager.getInstance(this@MyuLocPlaybackService).skipNext()
                                    }
                                }
                                clickCount = 0
                            }
                            return true
                        }
                    }
                    return super.onMediaButtonEvent(mediaButtonIntent)
                }

                override fun onPlay() {
                    MusicPlayerManager.getInstance(this@MyuLocPlaybackService).togglePlayPause()
                    scope.launch { updateNotification() }
                }

                override fun onPause() {
                    MusicPlayerManager.getInstance(this@MyuLocPlaybackService).togglePlayPause()
                    scope.launch { updateNotification() }
                }

                override fun onSkipToNext() {
                    MusicPlayerManager.getInstance(this@MyuLocPlaybackService).skipNext()
                    scope.launch { updateNotification() }
                }

                override fun onSkipToPrevious() {
                    MusicPlayerManager.getInstance(this@MyuLocPlaybackService).skipPrevious()
                    scope.launch { updateNotification() }
                }

                override fun onSeekTo(pos: Long) {
                    MusicPlayerManager.getInstance(this@MyuLocPlaybackService).seekTo(pos)
                    scope.launch { updateNotification() }
                }
            })
            isActive = true
        }

        scope.launch {
            MusicPlayerManager.getInstance(this@MyuLocPlaybackService).currentTrack.collect {
                updateNotification()
            }
        }
        scope.launch {
            MusicPlayerManager.getInstance(this@MyuLocPlaybackService).isPlaying.collect {
                updateNotification()
            }
        }
        scope.launch {
            MusicPlayerManager.getInstance(this@MyuLocPlaybackService).manualSeekEvents.collect {
                updateNotification()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                "ACTION_PLAY_PAUSE" -> MusicPlayerManager.getInstance(this).togglePlayPause()
                "ACTION_NEXT" -> MusicPlayerManager.getInstance(this).skipNext()
                "ACTION_PREVIOUS" -> MusicPlayerManager.getInstance(this).skipPrevious()
                "ACTION_SWIPED" -> {
                    val manager = MusicPlayerManager.getInstance(this)
                    // Swipe gesture on notification: skip next and update
                    manager.skipNext()
                    scope.launch { updateNotification() }
                }
                "ACTION_STOP" -> {
                    val manager = MusicPlayerManager.getInstance(this)
                    if (manager.isPlaying.value) {
                        manager.togglePlayPause()
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    } else {
                        @Suppress("DEPRECATION")
                        stopForeground(true)
                    }
                    stopSelf()
                }
            }
        }
        scope.launch {
            updateNotification()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        mediaSession.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        MusicPlayerManager.getInstance(this).release()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(Service.STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        stopSelf()
        kotlin.system.exitProcess(0)
    }

    @Synchronized
    private fun updateNotification() {
        val manager = MusicPlayerManager.getInstance(this)
        val track = manager.currentTrack.value ?: return

        val isPlaying = manager.isPlaying.value

        val stateBuilder = PlaybackState.Builder()
            .setActions(
                PlaybackState.ACTION_PLAY or
                PlaybackState.ACTION_PAUSE or
                PlaybackState.ACTION_SKIP_TO_NEXT or
                PlaybackState.ACTION_SKIP_TO_PREVIOUS or
                PlaybackState.ACTION_STOP or
                PlaybackState.ACTION_SEEK_TO
            )
            .setState(
                if (isPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED,
                manager.currentPosition.value,
                1.0f
            )
        mediaSession.setPlaybackState(stateBuilder.build())

        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = Notification.Action.Builder(
            android.graphics.drawable.Icon.createWithResource(
                this,
                if (isPlaying) com.example.R.drawable.ic_pause else com.example.R.drawable.ic_play
            ),
            if (isPlaying) "Pause" else "Play",
            PendingIntent.getService(
                this, 1,
                Intent(this, MyuLocPlaybackService::class.java).setAction("ACTION_PLAY_PAUSE"),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        ).build()

        val prevAction = Notification.Action.Builder(
            android.graphics.drawable.Icon.createWithResource(this, com.example.R.drawable.ic_skip_previous),
            "Previous",
            PendingIntent.getService(
                this, 2,
                Intent(this, MyuLocPlaybackService::class.java).setAction("ACTION_PREVIOUS"),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        ).build()

        val nextAction = Notification.Action.Builder(
            android.graphics.drawable.Icon.createWithResource(this, com.example.R.drawable.ic_skip_next),
            "Next",
            PendingIntent.getService(
                this, 3,
                Intent(this, MyuLocPlaybackService::class.java).setAction("ACTION_NEXT"),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        ).build()
        
        val deleteIntent = PendingIntent.getService(
            this, 4,
            Intent(this, MyuLocPlaybackService::class.java).setAction("ACTION_SWIPED"),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val bitmap = try {
            if (track.thumbnailUrl.isNotEmpty() && track.thumbnailUrl.startsWith("content://")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val uri = Uri.parse(track.thumbnailUrl)
                    android.graphics.ImageDecoder.decodeBitmap(android.graphics.ImageDecoder.createSource(contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    android.provider.MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(track.thumbnailUrl))
                }
            } else if (track.thumbnailUrl.isNotEmpty() && track.thumbnailUrl.startsWith("file://")) {
                BitmapFactory.decodeFile(track.thumbnailUrl.removePrefix("file://"))
            } else {
                null
            }
        } catch (e: Exception) { null }

        val metaDataBuilder = MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, track.title)
            .putString(MediaMetadata.METADATA_KEY_ARTIST, track.artist)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, manager.duration.value)

        if (bitmap != null) {
            metaDataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap)
        }
        mediaSession.setMetadata(metaDataBuilder.build())

        @Suppress("DEPRECATION")
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }
            .setSmallIcon(com.example.R.drawable.ic_music_note)
            .setContentTitle(track.title)
            .setContentText(track.artist)
            .setLargeIcon(bitmap)
            .setContentIntent(contentIntent)
            .setDeleteIntent(deleteIntent)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .addAction(prevAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .setStyle(
                Notification.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSession.sessionToken)
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
        }

        val notification = builder.build()
        if (isPlaying) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(Service.STOP_FOREGROUND_DETACH)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Playback Controls"
            val descriptionText = "Displays playback controls for currently running audio"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

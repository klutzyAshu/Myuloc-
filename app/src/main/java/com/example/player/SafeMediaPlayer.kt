package com.example.player

import android.media.MediaPlayer
import android.util.Log

/**
 * A robust, thread-safe wrapper around MediaPlayer that prevents IllegalStateException (e.g. error -38).
 */
class SafeMediaPlayer {
    private var mediaPlayer: MediaPlayer? = null
    
    // Core states based on Android's MediaPlayer State Machine
    enum class State {
        IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, PAUSED, STOPPED, ERROR
    }

    @Volatile
    private var currentState: State = State.IDLE

    init {
        initPlayer()
    }

    private fun initPlayer() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener { mp ->
                currentState = State.PREPARED
                onPrepared?.invoke(mp)
            }
            setOnErrorListener { mp, what, extra ->
                Log.e("SafeMediaPlayer", "Error: $what, $extra")
                currentState = State.ERROR
                // True means we handled the error (prevents onCompletion from being called incorrectly)
                onError?.invoke(mp, what, extra) ?: true 
            }
            setOnCompletionListener {
                // Return to PAUSED/PREPARED state functionally, or fire complete
                onCompletion?.invoke(it)
            }
        }
        currentState = State.IDLE
    }

    var onPrepared: ((MediaPlayer) -> Unit)? = null
    var onError: ((MediaPlayer, Int, Int) -> Boolean)? = null
    var onCompletion: ((MediaPlayer) -> Unit)? = null

    @Synchronized
    fun setDataSource(path: String) {
        if (currentState == State.IDLE) {
            mediaPlayer?.setDataSource(path)
            currentState = State.INITIALIZED
        } else {
            Log.w("SafeMediaPlayer", "Ignored setDataSource: wrong state ($currentState)")
        }
    }

    @Synchronized
    fun prepareAsync() {
        if (currentState == State.INITIALIZED || currentState == State.STOPPED) {
            currentState = State.PREPARING
            mediaPlayer?.prepareAsync()
        } else {
            Log.w("SafeMediaPlayer", "Ignored prepareAsync: wrong state ($currentState)")
        }
    }

    @Synchronized
    fun start() {
        if (currentState in listOf(State.PREPARED, State.STARTED, State.PAUSED)) {
            mediaPlayer?.start()
            currentState = State.STARTED
        } else {
            Log.w("SafeMediaPlayer", "Ignored start: safe guard prevented IllegalStateException")
        }
    }

    @Synchronized
    fun pause() {
        if (currentState in listOf(State.STARTED, State.PAUSED)) {
            mediaPlayer?.pause()
            currentState = State.PAUSED
        } else {
            Log.w("SafeMediaPlayer", "Ignored pause: safe guard prevented IllegalStateException")
        }
    }

    @Synchronized
    fun seekTo(msec: Int) {
        if (currentState in listOf(State.PREPARED, State.STARTED, State.PAUSED)) {
            mediaPlayer?.seekTo(msec)
        } else {
            Log.w("SafeMediaPlayer", "Ignored seekTo: wrong state ($currentState)")
        }
    }

    @Synchronized
    fun reset() {
        mediaPlayer?.reset()
        currentState = State.IDLE
    }

    @Synchronized
    fun safeRecover() {
        // Drop current instance and rebuild it smoothly if broken
        Log.i("SafeMediaPlayer", "Recovering player from ERROR state...")
        initPlayer()
    }
    
    @Synchronized
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentState = State.ERROR // Represents End
    }
}

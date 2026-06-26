package com.example.studymateandroidapp.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.annotation.RawRes

/**
 * Manages ambient-sound playback using [MediaPlayer].
 *
 * Design contract:
 *  - Created once (in ViewModel), reused across the timer lifecycle.
 *  - [play] creates a fresh MediaPlayer each time (previous one is released first).
 *  - [pause] / [resume] control the current player without re-creating it.
 *  - [stop] releases the player entirely.
 *  - [release] MUST be called from ViewModel.onCleared() to avoid leaks.
 *
 * @param context Application context — never an Activity to avoid leaks.
 */
class AmbientSoundManager(private val context: Context) {

    companion object {
        private const val TAG = "AmbientSoundManager"
    }

    private var mediaPlayer: MediaPlayer? = null
    private var currentResId: Int? = null

    /**
     * Start playing the ambient sound identified by [rawResId].
     * If a different sound is already playing, it is stopped and replaced.
     * If the *same* sound is already playing, this is a no-op.
     */
    fun play(@RawRes rawResId: Int) {
        // Already playing this exact sound — nothing to do.
        if (currentResId == rawResId && mediaPlayer?.isPlaying == true) return

        // Release any existing player before creating a new one.
        releasePlayer()

        try {
            mediaPlayer = MediaPlayer.create(context, rawResId)?.apply {
                isLooping = true
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what extra=$extra")
                    releasePlayer()
                    true
                }
                start()
            }
            currentResId = rawResId
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create MediaPlayer", e)
            releasePlayer()
        }
    }

    /** Pause playback. Safe to call even when nothing is playing. */
    fun pause() {
        try {
            mediaPlayer?.takeIf { it.isPlaying }?.pause()
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing", e)
        }
    }

    /** Resume a previously paused sound. */
    fun resume() {
        try {
            mediaPlayer?.takeIf { !it.isPlaying }?.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming", e)
        }
    }

    /** Stop playback and release the player. */
    fun stop() {
        releasePlayer()
    }

    /** Must be called from ViewModel.onCleared(). */
    fun release() {
        releasePlayer()
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing MediaPlayer", e)
        } finally {
            mediaPlayer = null
            currentResId = null
        }
    }
}

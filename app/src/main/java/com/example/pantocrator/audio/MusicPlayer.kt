package com.example.pantocrator.audio

import android.content.Context
import android.media.MediaPlayer
import com.example.pantocrator.R
import kotlin.random.Random

class MusicPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val playlist = listOf(
        R.raw.cancion1,
        R.raw.cancion2,
        R.raw.cancion3,
        R.raw.cancion4
    )
    private var currentSongIndex = -1

    fun startMusic() {
        if (mediaPlayer == null) {
            playNextSong()
        }
    }

    fun stopMusic() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }

    private fun playNextSong() {
        // Liberar el MediaPlayer anterior si existe
        mediaPlayer?.release()

        // Seleccionar una canción aleatoria diferente a la actual
        var nextIndex: Int
        do {
            nextIndex = Random.nextInt(playlist.size)
        } while (nextIndex == currentSongIndex && playlist.size > 1)
        
        currentSongIndex = nextIndex

        // Crear y configurar el nuevo MediaPlayer
        mediaPlayer = MediaPlayer.create(context, playlist[currentSongIndex]).apply {
            setOnCompletionListener {
                playNextSong() // Reproducir siguiente canción cuando termine
            }
            start()
        }
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
} 
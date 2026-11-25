package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.domain.model.AmbientTrack
import com.example.domain.model.GameCatalog
import com.example.domain.model.JewelType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

class AmbientAudioEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var ambientJob: Job? = null

    @Volatile
    private var ambientEnabled: Boolean = true

    @Volatile
    private var sfxEnabled: Boolean = true

    @Volatile
    private var currentTrack: AmbientTrack = GameCatalog.ambientTracks.first()

    @Volatile
    private var isForeground: Boolean = true

    private val sampleRate = 22050

    fun configure(ambientOn: Boolean, sfxOn: Boolean, trackId: String) {
        val trackChanged = currentTrack.id != trackId
        ambientEnabled = ambientOn
        sfxEnabled = sfxOn
        currentTrack = GameCatalog.trackById(trackId)

        if (!ambientEnabled || !isForeground) {
            stopAmbientLoop()
        } else if (ambientJob == null || trackChanged) {
            restartAmbientLoop()
        }
    }

    fun onLifecycleResume() {
        isForeground = true
        if (ambientEnabled) {
            restartAmbientLoop()
        }
    }

    fun onLifecyclePause() {
        isForeground = false
        stopAmbientLoop()
    }

    private fun restartAmbientLoop() {
        stopAmbientLoop()
        if (!ambientEnabled || !isForeground) return

        val track = currentTrack
        ambientJob = scope.launch {
            var audioTrack: AudioTrack? = null
            try {
                val minBufBytes = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(4096)

                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufBytes * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack.play()

                val chunkSize = 2048
                val buffer = ShortArray(chunkSize)
                var sampleIndex = 0L
                val freqs = track.chordFrequencies
                val shimmerPeriodSamples = (track.shimmerRateSeconds * sampleRate).toLong().coerceAtLeast(sampleRate.toLong())
                var shimmerFreq = freqs.last() * 2f
                var pinkState = 0.0

                while (isActive && ambientEnabled && isForeground) {
                    for (i in 0 until chunkSize) {
                        val t = sampleIndex.toDouble() / sampleRate.toDouble()

                        // Gentle breathing LFO
                        val breath = 0.55 + 0.45 * sin(2.0 * PI * 0.09 * t)
                        val fadeIn = (t / 1.8).coerceIn(0.0, 1.0)

                        // Warm harmonic pad from chord frequencies
                        var pad = 0.0
                        for (idx in freqs.indices) {
                            val f = freqs[idx].toDouble()
                            val weight = 1.0 / (idx + 1.2)
                            val detune = sin(2.0 * PI * (0.04 + idx * 0.013) * t) * 0.35
                            pad += sin(2.0 * PI * (f + detune) * t) * weight
                        }
                        pad /= freqs.size.toDouble()

                        // Soft filtered breeze texture
                        val white = (Random.nextDouble() * 2.0 - 1.0)
                        pinkState = pinkState * 0.985 + white * 0.015
                        val breeze = pinkState * 0.25

                        // Periodic soft pentatonic chime drop
                        val shimmerPos = sampleIndex % shimmerPeriodSamples
                        if (shimmerPos == 0L) {
                            val pick = freqs[Random.nextInt(freqs.size)]
                            shimmerFreq = if (Random.nextBoolean()) pick * 2f else pick * 3f
                        }
                        val shimmerTime = shimmerPos.toDouble() / sampleRate.toDouble()
                        val chimeEnv = if (shimmerTime < 1.8) {
                            (shimmerTime / 0.025).coerceAtMost(1.0) * exp(-shimmerTime * 2.4)
                        } else 0.0
                        val chime = sin(2.0 * PI * shimmerFreq * shimmerTime) * chimeEnv * 0.28

                        val mixed = ((pad * 0.52 * breath) + breeze + chime) * fadeIn * 0.22
                        val pcm = (mixed.coerceIn(-0.95, 0.95) * Short.MAX_VALUE).toInt().toShort()
                        buffer[i] = pcm
                        sampleIndex++
                    }
                    audioTrack.write(buffer, 0, chunkSize)
                }
            } catch (_: Throwable) {
                // Gracefully ignore if audio hardware is unavailable in headless/test environment
            } finally {
                try {
                    audioTrack?.pause()
                    audioTrack?.flush()
                    audioTrack?.release()
                } catch (_: Throwable) {
                }
            }
        }
    }

    private fun stopAmbientLoop() {
        ambientJob?.cancel()
        ambientJob = null
    }

    fun playMove() {
        if (!sfxEnabled || !isForeground) return
        playToneBurst(freqStart = 170f, freqEnd = 135f, durationMs = 28, volume = 0.14f)
    }

    fun playRotate() {
        if (!sfxEnabled || !isForeground) return
        playToneBurst(freqStart = 340f, freqEnd = 420f, durationMs = 42, volume = 0.18f)
    }

    fun playLock() {
        if (!sfxEnabled || !isForeground) return
        playToneBurst(freqStart = 195f, freqEnd = 110f, durationMs = 55, volume = 0.20f)
    }

    fun playLineClear(linesCount: Int) {
        if (!sfxEnabled || !isForeground) return
        val notes = when {
            linesCount >= 4 -> listOf(293.66f, 349.23f, 440f, 523.25f, 587.33f)
            linesCount == 3 -> listOf(293.66f, 349.23f, 440f, 587.33f)
            linesCount == 2 -> listOf(293.66f, 392.00f, 523.25f)
            else -> listOf(293.66f, 440f)
        }
        playArpeggio(notes, stepDelayMs = 48L, noteDurationMs = 210, volume = 0.24f)
    }

    fun playJewelPower(jewelType: JewelType) {
        if (!sfxEnabled || !isForeground) return
        val notes = when (jewelType) {
            JewelType.RUBY -> listOf(261.63f, 329.63f, 392f, 523.25f)
            JewelType.SAPPHIRE -> listOf(349.23f, 440f, 523.25f, 698.46f)
            JewelType.EMERALD -> listOf(293.66f, 392f, 493.88f, 587.33f)
            JewelType.AMETHYST -> listOf(329.63f, 440f, 554.37f, 659.25f)
            JewelType.NONE -> listOf(392f, 523.25f)
        }
        playArpeggio(notes, stepDelayMs = 42L, noteDurationMs = 280, volume = 0.28f)
    }

    fun playVictory() {
        if (!sfxEnabled || !isForeground) return
        playArpeggio(
            listOf(293.66f, 349.23f, 440f, 523.25f, 587.33f, 783.99f),
            stepDelayMs = 75L,
            noteDurationMs = 340,
            volume = 0.28f
        )
    }

    private fun playArpeggio(
        notes: List<Float>,
        stepDelayMs: Long,
        noteDurationMs: Int,
        volume: Float
    ) {
        scope.launch {
            for (note in notes) {
                playToneImmediate(note, note * 1.005f, noteDurationMs, volume)
                delay(stepDelayMs)
            }
        }
    }

    private fun playToneBurst(freqStart: Float, freqEnd: Float, durationMs: Int, volume: Float) {
        scope.launch {
            playToneImmediate(freqStart, freqEnd, durationMs, volume)
        }
    }

    private suspend fun playToneImmediate(
        freqStart: Float,
        freqEnd: Float,
        durationMs: Int,
        volume: Float
    ) {
        var track: AudioTrack? = null
        try {
            val totalSamples = ((sampleRate * durationMs) / 1000).coerceAtLeast(256)
            val samples = ShortArray(totalSamples)
            var phase = 0.0
            for (i in 0 until totalSamples) {
                val progress = i.toDouble() / totalSamples.toDouble()
                val freq = freqStart + (freqEnd - freqStart) * progress
                phase += 2.0 * PI * freq / sampleRate
                val attack = (progress / 0.08).coerceAtMost(1.0)
                val decay = exp(-progress * 4.5)
                val wave = (sin(phase) + 0.25 * sin(phase * 2.0)) * 0.8
                val v = (wave * attack * decay * volume).coerceIn(-0.95, 0.95)
                samples[i] = (v * Short.MAX_VALUE).toInt().toShort()
            }

            val byteCount = samples.size * 2
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(byteCount)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            delay(durationMs.toLong() + 35L)
        } catch (_: Throwable) {
            // Safe fallback if audio device unavailable
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Throwable) {
            }
        }
    }

    fun release() {
        stopAmbientLoop()
        scope.cancel()
    }
}

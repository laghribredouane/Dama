package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class DamaSoundManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)

    var soundEnabled: Boolean = true
    var hapticEnabled: Boolean = true

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun playMove() {
        if (!soundEnabled) return
        triggerHaptic(20)
        scope.launch {
            generateTone(
                frequencies = listOf(360f to 240f),
                durationMs = 60,
                amplitude = 0.35f
            )
        }
    }

    fun playCapture() {
        if (!soundEnabled) return
        triggerHaptic(45)
        scope.launch {
            generateTone(
                frequencies = listOf(620f to 180f),
                durationMs = 90,
                amplitude = 0.55f
            )
        }
    }

    fun playPromotion() {
        if (!soundEnabled) return
        triggerHaptic(60)
        scope.launch {
            // Majestic brass arpeggio (C5, E5, G5, C6)
            val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f)
            for (freq in notes) {
                generateTone(
                    frequencies = listOf(freq to freq),
                    durationMs = 80,
                    amplitude = 0.4f
                )
            }
        }
    }

    fun playVictory() {
        if (!soundEnabled) return
        triggerHaptic(80)
        scope.launch {
            val notes = listOf(587.33f, 659.25f, 880.00f, 1174.66f)
            for (freq in notes) {
                generateTone(
                    frequencies = listOf(freq to freq),
                    durationMs = 120,
                    amplitude = 0.5f
                )
            }
        }
    }

    fun playDefeat() {
        if (!soundEnabled) return
        triggerHaptic(60)
        scope.launch {
            val notes = listOf(440.0f, 415.3f, 392.0f, 349.2f)
            for (freq in notes) {
                generateTone(
                    frequencies = listOf(freq to freq),
                    durationMs = 140,
                    amplitude = 0.4f
                )
            }
        }
    }

    private fun triggerHaptic(durationMs: Long) {
        if (!hapticEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Ignore permission or hardware exceptions
        }
    }

    private fun generateTone(
        frequencies: List<Pair<Float, Float>>,
        durationMs: Int,
        amplitude: Float
    ) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val sample = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toFloat() / numSamples
                val (startFreq, endFreq) = frequencies.firstOrNull() ?: (440f to 440f)
                val currentFreq = startFreq + (endFreq - startFreq) * progress

                // Envelope: exponential decay for crisp wooden impact
                val envelope = (1.0f - progress) * (1.0f - progress)
                val time = i.toDouble() / sampleRate
                val wave = sin(2.0 * Math.PI * currentFreq * time)

                sample[i] = (wave * Short.MAX_VALUE * amplitude * envelope).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
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
                .setBufferSizeInBytes(sample.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(sample, 0, sample.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 20)
            audioTrack.release()
        } catch (_: Exception) {
            // AudioTrack failure fallback gracefully
        }
    }
}

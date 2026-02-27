package com.voxcom.chroniclesofvelmora.utils

import android.content.Context
import android.media.SoundPool
import android.media.AudioAttributes
import com.voxcom.chroniclesofvelmora.R

class SoundManager(context: Context) {

    private val soundPool: SoundPool
    val shootSound: Int
    val hitSound: Int
    val explosionSound: Int
    val jetpackSound: Int
    val reloadSound: Int

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attrs)
            .build()

        shootSound = soundPool.load(context, R.raw.shoot, 1)
        hitSound = soundPool.load(context, R.raw.hit, 1)
        explosionSound = soundPool.load(context, R.raw.explosion1, 1)
        jetpackSound = soundPool.load(context, R.raw.jetflame, 1)
        reloadSound = soundPool.load(context, R.raw.reload, 1)
    }

    fun playShoot() = soundPool.play(shootSound, 1f, 1f, 0, 0, 1f)
    fun playHit() = soundPool.play(hitSound, 1f, 1f, 0, 0, 1f)
    fun playExplosion() = soundPool.play(explosionSound, 1f, 1f, 0, 0, 1f)
    fun playJetpack() = soundPool.play(jetpackSound, 1f, 1f, 0, 0, 1f)
    fun playReload() = soundPool.play(reloadSound, 1f, 1f, 0, 0, 1f)

    fun release() = soundPool.release()
}
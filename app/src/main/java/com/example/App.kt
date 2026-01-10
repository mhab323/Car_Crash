package com.example

import android.app.Application
import com.example.cargame.R
import utilities.BackgroundMusicPlayer
import utilities.SoundEffectPlayer

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        setUpMusic()
    }

    private fun setUpMusic () {

        BackgroundMusicPlayer.init(this)
        SoundEffectPlayer.init(this)

        SoundEffectPlayer.load(this, R.raw.crash)
        SoundEffectPlayer.load(this, R.raw.game_over)
        SoundEffectPlayer.load(this, R.raw.collectcoin)
    }




}
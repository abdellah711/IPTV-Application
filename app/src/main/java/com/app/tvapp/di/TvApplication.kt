package com.app.tvapp.di

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TvApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val theme =
            when ((PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("themes_key", "-1")
                ?: "-1").toInt()) {
                0 -> AppCompatDelegate.MODE_NIGHT_YES

                1 -> AppCompatDelegate.MODE_NIGHT_NO

                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        if (theme == AppCompatDelegate.getDefaultNightMode()) return
        AppCompatDelegate.setDefaultNightMode(theme)
    }
}
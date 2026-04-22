package com.geotask

import android.app.Application
import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GeoTaskApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val config = org.osmdroid.config.Configuration.getInstance()
        config.load(this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this))
        config.userAgentValue = "GeoTask/1.0"
    }
}
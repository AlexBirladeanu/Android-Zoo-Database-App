package com.example.zoomies.model.observer

import android.content.res.Configuration
import android.content.res.Resources
import android.widget.Toast
import com.example.zoomies.MainActivity
import java.util.*

class LanguageEventHandler : Subject() {
    fun setLocale(activity: MainActivity, languageIndex: Int) {
        activeLanguagePrefix = when (languageIndex) {
            0 -> "en"
            1 -> "ro"
            else -> "fr"
        }
        val locale = Locale(activeLanguagePrefix)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.getConfiguration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.getDisplayMetrics())
        Toast.makeText(activity, "Language change to $activeLanguagePrefix", Toast.LENGTH_LONG).show()

        callObservers()
    }

    companion object {
        var activeLanguagePrefix = "en"
    }
}